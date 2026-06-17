package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.api.data.DanmakuData
import com.muedsa.tvbox.api.data.DanmakuDataFlow
import com.muedsa.tvbox.api.data.MediaDetail
import com.muedsa.tvbox.api.data.MediaEpisode
import com.muedsa.tvbox.api.data.MediaHttpSource
import com.muedsa.tvbox.api.data.MediaPlaySource
import com.muedsa.tvbox.api.data.SavedMediaCard
import com.muedsa.tvbox.api.service.IMediaDetailService
import com.muedsa.tvbox.api.store.IPluginPerfStore
import com.muedsa.tvbox.douyu.DouyuConst
import com.muedsa.tvbox.douyu.DouyuHelper
import com.muedsa.tvbox.douyu.model.DouyuApiResp
import com.muedsa.tvbox.douyu.model.H5PlayInfo
import com.muedsa.tvbox.douyu.model.LiveConfig
import com.muedsa.tvbox.tool.toRequestBuild
import okhttp3.OkHttpClient
import kotlin.random.Random

class MediaDetailService(
    private val douyuService: DouyuService,
    private val okHttpClient: OkHttpClient,
    private val store: IPluginPerfStore,
) : IMediaDetailService {

    private val actionDelegate = ActionDelegate(
        store = store,
    )

    override suspend fun getDetailData(mediaId: String, detailUrl: String): MediaDetail {
        if (mediaId.startsWith(ActionDelegate.ACTION_PREFIX)) {
            return actionDelegate.exec(action = mediaId, data = detailUrl)
        }

        val rid: Long = mediaId.toLong()
        val betard = douyuService.roomBetard(rid)
        if (betard.room.roomId < 0) {
            throw RuntimeException("查询${mediaId}房间betard信息失败")
        }
        val playSourceList = if (betard.room.showStatus == 1) {
            try {
                val playInfo = getPlayInfo(
                    rid = rid,
                    did = DouyuConst.DID,
                    liveConfig = actionDelegate.getLiveConfig(),
                )
                val episodes = playInfo.multiRates.map { r ->
                    MediaEpisode(
                        id = r.rate.toString(),
                        name = r.name,
                        flag1 = r.rate,
                        flag3 = rid,
                    )
                }
                playInfo.cdnsWithName.map { cdn ->
                    MediaPlaySource(
                        id = cdn.cdn,
                        name = cdn.name,
                        episodeList = episodes
                    )
                }
            } catch (_: Throwable) {
                emptyList()
            }
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
            ),
            disableEpisodeProgression = true,
            enableCustomDanmakuFlow = true,
        )
    }

    override suspend fun getEpisodePlayInfo(
        playSource: MediaPlaySource,
        episode: MediaEpisode
    ): MediaHttpSource {
        val cdn = playSource.id
        val rid = episode.flag3 ?: throw RuntimeException("rid 为空")
        val rate = episode.flag1 ?: throw RuntimeException("分辨率为空")
        val playInfo = getPlayInfo(
            rid = rid,
            did = DouyuConst.DID,
            cdn = cdn,
            rate = rate,
            liveConfig = actionDelegate.getLiveConfig(),
        )
        val rtmpUrl = playInfo.rtmpUrl
        val rtmpLive = playInfo.rtmpLive
        return MediaHttpSource(
            url = "${rtmpUrl}/${rtmpLive}",
            httpHeaders = mapOf(
                "User-Agent" to DouyuConst.IOS_USER_AGENT,
                "Referer" to "${DouyuConst.PC_URL}${rid}",
            )
        )
    }

    suspend fun getPlayInfo(
        rid: Long,
        did: String,
        cdn: String = "",
        rate: Int = -1,
        liveConfig: LiveConfig,
    ): H5PlayInfo {
        var info: H5PlayInfo? = try {
            getNewPlayInfo(
                rid = rid,
                did = did,
                cdn = cdn,
                rate = rate,
                liveConfig = liveConfig,
            ).data
        } catch (_: Throwable) {
            null
        }
        if (info == null) {
            info = getOldPlayInfo(
                rid = rid,
                did = did,
                cdn = cdn,
                rate = rate,
                liveConfig = liveConfig,
            ).data
        }
        return info ?: throw RuntimeException("获取播放地址失败")
    }

    suspend fun getOldPlayInfo(
        rid: Long,
        did: String,
        cdn: String = "",
        rate: Int = -1,
        liveConfig: LiveConfig,
    ): DouyuApiResp<H5PlayInfo> {
        val h5EncResp = douyuService.homeH5Enc(rid)
        check(h5EncResp.error == 0) { h5EncResp.msg ?: "查询h5enc失败" }
        checkNotNull(h5EncResp.data) { "查询h5enc失败，返回值为空" }
        val jsCode = h5EncResp.data["room${rid}"]
        checkNotNull(jsCode) { "查询h5enc失败，jsCode为空" }
        val signParms = DouyuHelper.buildSignParams(
            js = jsCode,
            rid = rid,
            did = did,
        )
        return douyuService.getH5Play(
            rid = rid,
            signParams = signParms,
            cdn = cdn,
            rate = rate,
            hevc = liveConfig.hevc
        )
    }

    suspend fun getNewPlayInfo(
        rid: Long,
        did: String,
        cdn: String = "",
        rate: Int = -1,
        liveConfig: LiveConfig,
    ): DouyuApiResp<H5PlayInfo> {
        val encryptionInfo = DouyuHelper.getEncryptionInfo(
            did = did,
            douyuService = douyuService,
            store = store,
        )
        val signParams = DouyuHelper.buildWebStreamSignParams(
            encryptionInfo = encryptionInfo,
            rid = rid,
            did = did,
            ts = System.currentTimeMillis() / 1000
        )
        return douyuService.getH5PlayV1(
            rid = rid,
            signParams = signParams,
            cdn = cdn,
            rate = rate,
            hevc = liveConfig.hevc
        )
    }

    override suspend fun getEpisodeDanmakuDataList(episode: MediaEpisode): List<DanmakuData>
        = emptyList()

    override suspend fun getEpisodeDanmakuDataFlow(episode: MediaEpisode): DanmakuDataFlow? {
        val roomId = episode.flag3 ?: return null
        val rand = Random.nextInt(1, 5)
        return LiveDanmakuDataFlow(
            roomId = roomId,
            request = "wss://danmuproxy.douyu.com:850${rand}".toRequestBuild().build(),
            okHttpClient = okHttpClient,
        )
    }
}