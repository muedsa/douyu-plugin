package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.api.data.MediaCard
import com.muedsa.tvbox.api.data.MediaCardRow
import com.muedsa.tvbox.api.data.MediaCardType
import com.muedsa.tvbox.api.data.MediaDetail
import com.muedsa.tvbox.api.store.IPluginPerfStore
import com.muedsa.tvbox.douyu.DebugHelper
import com.muedsa.tvbox.douyu.PERF_KEY_LIVE_CONFIG_HEVC
import com.muedsa.tvbox.douyu.model.LiveConfig
import timber.log.Timber

class ActionDelegate(
    private val store: IPluginPerfStore,
) {

    suspend fun exec(action: String, data: String): MediaDetail {
        val mediaDetail = when (action) {
            ACTION_LIVE_CONFIG -> {
                val updateConfig = if (data.startsWith("$ACTION_LIVE_CONFIG:")) {
                    data.removePrefix("$ACTION_LIVE_CONFIG:")
                } else null
                liveConfig(updateConfig)
            }

            ACTION_DEBUG_STORE -> {
                Timber.i(DebugHelper.toJsonStr(store))
                throw IllegalArgumentException("DEBUG STORE")
            }

            ACTION_INVALID -> throw IllegalArgumentException("这是一个动作卡片,请删除")

            else -> throw IllegalArgumentException("未知动作")
        }
        return mediaDetail
    }

    private suspend fun liveConfig(updateConfig: String?): MediaDetail {
        if (!updateConfig.isNullOrBlank()) {
            val (key, value) = updateConfig.split(":")
            updateLiveConfig(key, value)
        }
        return createLiveConfigMediaDetail(getLiveConfig())
    }

    private suspend fun updateLiveConfig(key: String, value: String) {
        when(key) {
            "hevc" -> {
                store.update(key = PERF_KEY_LIVE_CONFIG_HEVC, value = value.toInt())
            }
        }
    }

    suspend fun getLiveConfig(): LiveConfig {
        return LiveConfig(
            hevc = store.getOrDefault(key = PERF_KEY_LIVE_CONFIG_HEVC, default = 1)
        )
    }

    companion object {
        const val ACTION_PREFIX = "action_"
        const val ACTION_INVALID = "${ACTION_PREFIX}invalid"
        const val ACTION_DEBUG_STORE = "${ACTION_PREFIX}debug_store"
        const val ACTION_LIVE_CONFIG = "${ACTION_PREFIX}live_config"

        val LIVE_CONFIG_ACTION_CARD = MediaCard(
            id = ACTION_LIVE_CONFIG,
            title = "设置",
            subTitle = "设置",
            detailUrl = ACTION_LIVE_CONFIG,
        )

        val DEBUG_STORE_ACTION_CARD = MediaCard(
            id = ACTION_DEBUG_STORE,
            title = "DEBUG STORE",
            subTitle = "打印Store",
            detailUrl = ACTION_DEBUG_STORE,
        )

        val INVALID_ACTION_SAVED_MEDIA_CARD = null

        fun createLiveConfigMediaDetail(liveConfig: LiveConfig): MediaDetail {
            return MediaDetail(
                id = ACTION_LIVE_CONFIG,
                title = "直播设置",
                detailUrl = ACTION_LIVE_CONFIG,
                subTitle = "直播设置",
                backgroundImageUrl = "",
                description = " ● HEVC编码：现代化的H.265视频编码格式，如果视频黑屏或无法播放，尝试关闭HEVC编码",
                playSourceList = listOf(),
                favoritedMediaCard = INVALID_ACTION_SAVED_MEDIA_CARD,
                disableEpisodeProgression = true,
                rows = listOf(
                    MediaCardRow(
                        title = "设置",
                        list = listOf(
                            MediaCard(
                                id = ACTION_LIVE_CONFIG,
                                title = "HEVC编码",
                                subTitle = "${if (liveConfig.hevc == 1) "已开启" else "已关闭"}HEVC编码",
                                detailUrl = "$ACTION_LIVE_CONFIG:hevc:${if (liveConfig.hevc == 1) 0 else 1}",
                                backgroundColor = if (liveConfig.hevc == 1) 0xFF_05_B3_73 else 0xFF_6D_7B_8D,
                            ),
                        ),
                        cardWidth = 240,
                        cardHeight = 60,
                        cardType = MediaCardType.NOT_IMAGE
                    )
                )
            )
        }
    }
}