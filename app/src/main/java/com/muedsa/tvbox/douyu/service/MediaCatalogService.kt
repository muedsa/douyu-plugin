package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.api.data.MediaCard
import com.muedsa.tvbox.api.data.MediaCatalogConfig
import com.muedsa.tvbox.api.data.MediaCatalogOption
import com.muedsa.tvbox.api.data.MediaCatalogOptionItem
import com.muedsa.tvbox.api.data.PagingResult
import com.muedsa.tvbox.api.service.IMediaCatalogService
import com.muedsa.tvbox.douyu.DouyuConst
import com.muedsa.tvbox.douyu.model.CategoryList
import timber.log.Timber

class MediaCatalogService(
    val douyuMobileService: DouyuMobileService,
) : IMediaCatalogService {

    override suspend fun getConfig(): MediaCatalogConfig {
        val options = try {
            val resp = douyuMobileService.categoryList()
            resp.data?.let { buildOptions(it) } ?: emptyList()
        } catch (throwable: Throwable) {
            Timber.e(throwable, "categoryList error")
            emptyList()
        }
        return MediaCatalogConfig(
            initKey = "1",
            pageSize = 20,
            cardWidth = DouyuConst.CARD_WIDTH,
            cardHeight = DouyuConst.CARD_HEIGHT,
            catalogOptions = options,
        )
    }

    override suspend fun catalog(
        options: List<MediaCatalogOption>,
        loadKey: String,
        loadSize: Int
    ): PagingResult<MediaCard> {
        val page = loadKey.toInt()
        if (options.isEmpty() || options[0].items.isEmpty()) return PagingResult(
            list = emptyList(),
            nextKey = null,
            prevKey = null
        )
        val item = options[0].items[0]
        val resp = douyuMobileService.category2LiveList(
            cate2 = item.value,
            offset = page,
            limit = loadSize,
        )
        val list = resp.data?.list?.map { r ->
            val ridStr = r.rid.toString()
            MediaCard(
                id = ridStr,
                title = r.roomName,
                subTitle = r.nickname,
                detailUrl = ridStr,
                coverImageUrl = r.roomSrc, // 320 * 180
            )
        }
        return PagingResult(
            list = list ?: emptyList(),
            nextKey = if (list.isNullOrEmpty()) null else "${page + 1}",
            prevKey = if (page <= 1) null else "${page - 1}"
        )
    }

    companion object {
        fun buildOptions(categoryList: CategoryList): List<MediaCatalogOption> {
            val cate2Map = categoryList.cate2Info.groupBy { it.cate1Id }
            return categoryList.cate1Info.mapNotNull { c1 ->
                val c2List = cate2Map[c1.cate1Id] ?: return@mapNotNull null
                MediaCatalogOption(
                    name = c1.cate1Name,
                    value = c1.shortName,
                    items = c2List.map { c2 ->
                        MediaCatalogOptionItem(
                            name = c2.cate2Name,
                            value = c2.shortName,
                        )
                    }
                )
            }
        }
    }
}