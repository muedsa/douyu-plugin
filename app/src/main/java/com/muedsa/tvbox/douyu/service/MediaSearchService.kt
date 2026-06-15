package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.api.data.MediaCardRow
import com.muedsa.tvbox.api.service.IMediaSearchService
import com.muedsa.tvbox.douyu.DouyuConst

class MediaSearchService : IMediaSearchService {
    override suspend fun searchMedias(query: String): MediaCardRow {
        return MediaCardRow(
            title = "search list",
            cardWidth = DouyuConst.CARD_WIDTH,
            cardHeight = DouyuConst.CARD_HEIGHT,
            list = emptyList()
        )
    }
}