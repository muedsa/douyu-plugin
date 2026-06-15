package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.api.data.MediaCard
import com.muedsa.tvbox.api.data.MediaCatalogConfig
import com.muedsa.tvbox.api.data.MediaCatalogOption
import com.muedsa.tvbox.api.data.MediaCatalogOptionItem
import com.muedsa.tvbox.api.data.PagingResult
import com.muedsa.tvbox.api.service.IMediaCatalogService
import com.muedsa.tvbox.douyu.DouyuConst
import java.util.Calendar

class MediaCatalogService: IMediaCatalogService {

    override suspend fun getConfig(): MediaCatalogConfig {
        return MediaCatalogConfig(
            initKey = "1",
            pageSize = 20,
            cardWidth = DouyuConst.CARD_WIDTH,
            cardHeight = DouyuConst.CARD_HEIGHT,
            catalogOptions = listOf()
        )
    }

    override suspend fun catalog(
        options: List<MediaCatalogOption>,
        loadKey: String,
        loadSize: Int
    ): PagingResult<MediaCard> {
        return PagingResult(
            list = emptyList(),
            nextKey = null,
            prevKey = null
        )
    }
}