package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.api.data.DanmakuData
import com.muedsa.tvbox.api.data.DanmakuDataFlow
import com.muedsa.tvbox.api.data.MediaDetail
import com.muedsa.tvbox.api.data.MediaEpisode
import com.muedsa.tvbox.api.data.MediaHttpSource
import com.muedsa.tvbox.api.data.MediaPlaySource
import com.muedsa.tvbox.api.data.SavedMediaCard
import com.muedsa.tvbox.api.service.IMediaDetailService
import com.muedsa.tvbox.douyu.DouyuConst
import com.muedsa.tvbox.douyu.DouyuHelper

class MediaDetailService(
    private val douyuService: DouyuService,
) : IMediaDetailService {

    override suspend fun getDetailData(mediaId: String, detailUrl: String): MediaDetail {
        val rid: Long = mediaId.toLong()
        val betard = douyuService.roomBetard(rid)
        if (betard.room.roomId < 0) {
            throw RuntimeException("查询${mediaId}房间betard信息失败")
        }
        val playSourceList = if (betard.room.showStatus == 1) {
            val h5EncResp = douyuService.homeH5Enc(rid)
            check(h5EncResp.error == 0) { h5EncResp.msg ?: "查询h5enc失败" }
            checkNotNull(h5EncResp.data){ "查询h5enc失败，返回值为空" }
            val jsCode = h5EncResp.data["room${rid}"]
            checkNotNull(jsCode){ "查询h5enc失败，jsCode为空" }
            val signParms = DouyuHelper.buildSignParms(jsCode, rid)
            val playInfoResp = douyuService.getH5Play(rid = rid, signParams = signParms)
            val episodes = playInfoResp.data?.multiRates?.map {  r ->
                MediaEpisode(
                    id = r.rate.toString(),
                    name = r.name,
                    flag1 = r.rate,
                    flag3 = rid,
                )
            } ?: emptyList()
            playInfoResp.data?.cdnsWithName?.map {  cdn ->
                MediaPlaySource(
                    id = cdn.cdn,
                    name = cdn.name,
                    episodeList = episodes
                )
            } ?: emptyList()
        } else emptyList()
        return MediaDetail(
            id = mediaId,
            title = if (betard.room.showStatus == 1) betard.room.roomName else "未开播",
            subTitle = betard.room.nickname,
            description = betard.seoInfo.seoDescription,
            detailUrl = mediaId,
            backgroundImageUrl = betard.room.roomPic,
            playSourceList = playSourceList,
            favoritedMediaCard = SavedMediaCard(
                id = mediaId,
                title = betard.room.nickname,
                detailUrl = mediaId,
                coverImageUrl = betard.room.coverSrc,
                cardWidth = DouyuConst.CARD_WIDTH,
                cardHeight = DouyuConst.CARD_HEIGHT,
            )
        )
    }

    override suspend fun getEpisodePlayInfo(
        playSource: MediaPlaySource,
        episode: MediaEpisode
    ): MediaHttpSource {
        val cdn = playSource.id
        val rid = episode.flag3 ?: throw RuntimeException("rid 为空")
        val rate = episode.flag1 ?: throw RuntimeException("分辨率为空")
        val h5EncResp = douyuService.homeH5Enc(rid)
        check(h5EncResp.error == 0) { h5EncResp.msg ?: "查询h5enc失败" }
        checkNotNull(h5EncResp.data){ "查询h5enc失败，返回值为空" }
        val jsCode = h5EncResp.data["room${rid}"]
        checkNotNull(jsCode){ "查询h5enc失败，jsCode为空" }
        val signParms = DouyuHelper.buildSignParms(jsCode, rid)
        val resp = douyuService.getH5Play(
            rid = rid,
            signParams = signParms,
            cdn = cdn,
            rate = rate,
        )
        if (resp.error != 0) {
            throw RuntimeException(resp.msg ?: "获取播放地址失败")
        }
        val rtmpUrl = resp.data?.rtmpUrl ?: throw RuntimeException("获取播放地址rtmpUrl失败")
        val rtmpLive = resp.data.rtmpLive
        return MediaHttpSource(
            url = "${rtmpUrl}/${rtmpLive}",
            httpHeaders = mapOf(
                "User-Agent" to DouyuConst.IOS_USER_AGENT,
                "Referer" to "${DouyuConst.PC_URL}${rid}",
            )
        )
    }

    override suspend fun getEpisodeDanmakuDataList(episode: MediaEpisode): List<DanmakuData>
        = emptyList()

    override suspend fun getEpisodeDanmakuDataFlow(episode: MediaEpisode): DanmakuDataFlow? = null
}