package com.muedsa.tvbox.demoplugin.service

import com.muedsa.tvbox.api.data.MediaCard
import com.muedsa.tvbox.api.data.MediaCardRow
import com.muedsa.tvbox.api.service.IMediaSearchService
import com.muedsa.tvbox.demoplugin.model.SearchSubjectsFilter
import com.muedsa.tvbox.demoplugin.model.SearchSubjectsParams

class MediaSearchService(
    private val bangumiApiService: BangumiApiService,
) : IMediaSearchService {
    override suspend fun searchMedias(query: String): MediaCardRow {
        val params = SearchSubjectsParams(
            keyword = query,
            filter = SearchSubjectsFilter(
                type = listOf(2)
            )
        )
        val resp = bangumiApiService.searchSubjects(
            body = params,
            offset = 0,
            limit = 20,
        )
        return MediaCardRow(
            title = "search list",
            cardWidth = 150,
            cardHeight = 212,
            list = resp.data.map {
                MediaCard(
                    id = it.id.toString(),
                    title = it.nameCn.ifBlank { it.name },
                    subTitle = it.platform,
                    detailUrl = it.id.toString(),
                    coverImageUrl = it.images.large,
                )
            }
        )
    }
}