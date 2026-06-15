package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.api.data.MediaCard
import com.muedsa.tvbox.api.data.MediaCardRow
import com.muedsa.tvbox.api.service.IMainScreenService
import com.muedsa.tvbox.douyu.DouyuConst

class MainScreenService(
    private val douyuService: DouyuService,
    private val douyuMobileService: DouyuMobileService,
) : IMainScreenService {

    override suspend fun getRowsData(): List<MediaCardRow> {
        val webCategoryListResp = douyuService.webCategoryList()
        if (webCategoryListResp.error != 0) {
            throw RuntimeException("请求webCategoryList失败，${webCategoryListResp.msg ?: "error"}")
        }
        val hotCategoryList = webCategoryListResp.data
            ?.cateList
            ?.find { it.name == "热门分类" }
            ?.list
            ?: emptyList()

        return hotCategoryList.mapNotNull { c ->
            val cate2ShortName = c.url.removePrefix("/g_")
            val roomListResp = douyuMobileService.category2LiveList(
                cate2 = cate2ShortName,
            )
            val cards = roomListResp.data?.list?.map { r ->
                val ridStr = r.rid.toString()
                MediaCard(
                    id = ridStr,
                    title = r.roomName,
                    subTitle = r.nickname,
                    detailUrl = ridStr,
                    coverImageUrl = r.roomSrc, // 320 * 180
                )
            }
            if (!cards.isNullOrEmpty()) {
                MediaCardRow(
                    title = c.name,
                    cardWidth = DouyuConst.CARD_WIDTH,
                    cardHeight = DouyuConst.CARD_HEIGHT,
                    list = cards
                )
            } else null
        }
    }
}