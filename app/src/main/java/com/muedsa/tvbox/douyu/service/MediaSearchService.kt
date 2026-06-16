package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.api.data.MediaCard
import com.muedsa.tvbox.api.data.MediaCardRow
import com.muedsa.tvbox.api.service.IMediaSearchService
import com.muedsa.tvbox.douyu.DouyuConst

class MediaSearchService(
    private val douyuService: DouyuService,
) : IMediaSearchService {
    override suspend fun searchMedias(query: String): MediaCardRow {
        if (!ROOM_ID_REGEX.matches(query)) {
            throw RuntimeException("请输入房间号搜索")
        }
        val rid: Long = query.toLong()
        val betard = douyuService.roomBetard(rid)
        if (betard.room.roomId < 0) {
            throw RuntimeException("查询${query}房间betard信息失败")
        }
        return MediaCardRow(
            title = "search list",
            cardWidth = DouyuConst.CARD_WIDTH,
            cardHeight = DouyuConst.CARD_HEIGHT,
            list = listOf(
                MediaCard(
                    id = query,
                    title = betard.room.roomName,
                    subTitle = betard.room.nickname,
                    detailUrl = query,
                    coverImageUrl = betard.room.coverSrc,
                )
            )
        )
    }

    companion object {
        val ROOM_ID_REGEX = "^\\d+$".toRegex()
    }
}