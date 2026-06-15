package com.muedsa.tvbox.demoplugin.service

import com.muedsa.tvbox.api.data.DanmakuData
import com.muedsa.tvbox.api.data.DanmakuDataFlow
import com.muedsa.tvbox.api.data.MediaDetail
import com.muedsa.tvbox.api.data.MediaEpisode
import com.muedsa.tvbox.api.data.MediaHttpSource
import com.muedsa.tvbox.api.data.MediaPlaySource
import com.muedsa.tvbox.api.data.SavedMediaCard
import com.muedsa.tvbox.api.service.IMediaDetailService

class MediaDetailService(
    private val bangumiApiService: BangumiApiService,
) : IMediaDetailService {

    override suspend fun getDetailData(mediaId: String, detailUrl: String): MediaDetail {
        val subject = bangumiApiService.subject(subjectId = mediaId.toLong())
        val episodeFlow = bangumiApiService.episodes(
            subjectId = mediaId.toLong(),
            offset = 0,
            limit = if (subject.eps < 100) subject.eps else 100,
        )
        return MediaDetail(
            id = subject.id.toString(),
            title = subject.nameCn.ifBlank { subject.name },
            subTitle = subject.metaTags.joinToString(" | "),
            description = subject.summary,
            detailUrl = subject.id.toString(),
            backgroundImageUrl = subject.images.large,
            playSourceList = listOf(
                MediaPlaySource(
                    id = "bangumi",
                    name = "bangumi",
                    episodeList = episodeFlow.data.mapIndexed { index, item ->
                        var name = item.nameCn
                        if (name.isBlank()) {
                            name = item.name
                        }
                        if (name.isBlank()) {
                            name = if (subject.platform == "剧场版") {
                                subject.nameCn.ifBlank { subject.name }
                            } else {
                                "第${index + 1}集"
                            }
                        }
                        MediaEpisode(
                            id = item.id.toString(),
                            name = name,
                        )
                    }
                )
            ),
            favoritedMediaCard = SavedMediaCard(
                id = subject.id.toString(),
                title = subject.nameCn.ifBlank { subject.name },
                detailUrl = subject.id.toString(),
                coverImageUrl = subject.images.large,
                cardWidth = 150,
                cardHeight = 212,
            )
        )
    }

    override suspend fun getEpisodePlayInfo(
        playSource: MediaPlaySource,
        episode: MediaEpisode
    ): MediaHttpSource = MediaHttpSource(url = "https://media.w3.org/2010/05/sintel/trailer.mp4")

    override suspend fun getEpisodeDanmakuDataList(episode: MediaEpisode): List<DanmakuData>
        = emptyList()

    override suspend fun getEpisodeDanmakuDataFlow(episode: MediaEpisode): DanmakuDataFlow? = null
}