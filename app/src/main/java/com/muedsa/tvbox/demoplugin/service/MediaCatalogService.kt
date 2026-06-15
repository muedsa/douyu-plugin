package com.muedsa.tvbox.demoplugin.service

import com.muedsa.tvbox.api.data.MediaCard
import com.muedsa.tvbox.api.data.MediaCatalogConfig
import com.muedsa.tvbox.api.data.MediaCatalogOption
import com.muedsa.tvbox.api.data.MediaCatalogOptionItem
import com.muedsa.tvbox.api.data.PagingResult
import com.muedsa.tvbox.api.service.IMediaCatalogService
import java.util.Calendar

class MediaCatalogService(
    private val bangumiApiService: BangumiApiService,
) : IMediaCatalogService {

    override suspend fun getConfig(): MediaCatalogConfig {
        return MediaCatalogConfig(
            initKey = "1",
            pageSize = 20,
            cardWidth = 150,
            cardHeight = 212,
            catalogOptions = listOf(
                MediaCatalogOption(
                    name = "类型",
                    value = "cat",
                    items = listOf(
                        MediaCatalogOptionItem(
                            name = "TV",
                            value = "1",
                        ),
                        MediaCatalogOptionItem(
                            name = "OVA",
                            value = "2",
                        ),
                        MediaCatalogOptionItem(
                            name = "Movie",
                            value = "3",
                        ),
                        MediaCatalogOptionItem(
                            name = "WEB",
                            value = "5",
                        ),
                    ),
                    required = false
                ),
                MediaCatalogOption(
                    name = "年份",
                    value = "year",
                    items = buildList {
                        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                        for (year in 2000..currentYear) {
                            add(
                                MediaCatalogOptionItem(
                                    name = year.toString(),
                                    value = year.toString(),
                                )
                            )
                        }
                    }.reversed(),
                    required = false
                ),
                MediaCatalogOption(
                    name = "月份",
                    value = "month",
                    items = buildList {
                        for (month in 1..12) {
                            add(
                                MediaCatalogOptionItem(
                                    name = month.toString(),
                                    value = month.toString(),
                                )
                            )
                        }
                    },
                    required = false
                ),
                MediaCatalogOption(
                    name = "排序",
                    value = "sort",
                    items = listOf(
                        MediaCatalogOptionItem(
                            name = "时间",
                            value = "date",
                            defaultChecked = true
                        ),
                        MediaCatalogOptionItem(
                            name = "评分",
                            value = "rank",
                        )
                    ),
                    required = true
                ),
                MediaCatalogOption(
                    name = "Other",
                    value = "other",
                    items = buildList {
                        for (i in 0..8) {
                            add(
                                MediaCatalogOptionItem(
                                    name = "other$i",
                                    value = i.toString(),
                                )
                            )
                        }
                    },
                    multiple = true
                )
            )
        )
    }

    override suspend fun catalog(
        options: List<MediaCatalogOption>,
        loadKey: String,
        loadSize: Int
    ): PagingResult<MediaCard> {
        val cat = options.find { option -> option.value == "cat" }?.items[0]?.value
        val year = options.find { option -> option.value == "year" }?.items[0]?.value
        val month = options.find { option -> option.value == "month" }?.items[0]?.value
        val sort = options.find { option -> option.value == "sort" }?.items[0]?.value
            ?: throw RuntimeException("排序为必选项")
        val flow = bangumiApiService.subjects(
            type = 2,
            cat = cat?.toInt(),
            year = year?.toInt(),
            month = month?.toInt(),
            sort = sort,
            offset = loadKey.toInt(),
            limit = loadSize,
        )
        return PagingResult(
            list = flow.data.map {
                MediaCard(
                    id = it.id.toString(),
                    title = it.nameCn.ifBlank { it.name },
                    subTitle = it.platform,
                    detailUrl = it.id.toString(),
                    coverImageUrl = it.images.large,
                )
            },
            nextKey = if (flow.data.isNotEmpty()) (flow.offset + flow.data.size).toString() else null,
            prevKey = null
        )
    }
}