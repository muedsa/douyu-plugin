package com.muedsa.tvbox.demoplugin.service

import com.muedsa.tvbox.api.data.MediaCard
import com.muedsa.tvbox.api.data.MediaCardRow
import com.muedsa.tvbox.api.service.IMainScreenService

class MainScreenService(
    private val bangumiApiService: BangumiApiService,
) : IMainScreenService {

    override suspend fun getRowsData(): List<MediaCardRow> {
        val list = bangumiApiService.calendar()
        return list.map {
            MediaCardRow(
                title = "每日放送 ${it.weekday.cn}",
                cardWidth = 150,
                cardHeight = 212,
                list = it.items.map { b ->
                    MediaCard(
                        id = b.id.toString(),
                        title = b.nameCn.ifBlank { b.name },
                        subTitle = b.airDate,
                        detailUrl = b.id.toString(),
                        coverImageUrl = b.images.large,
                    )
                }
            )
        }
    }
}