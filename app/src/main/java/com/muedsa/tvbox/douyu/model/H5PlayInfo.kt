package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class H5PlayInfo(
    @SerialName("room_id") val roomId: Long = 0,
    @SerialName("rtmp_cdn") val rtmpCDN: String = "",
    @SerialName("rtmp_url") val rtmpUrl: String = "",
    @SerialName("rtmp_live") val rtmpLive: String = "",
    val rate: Int = -1,
    @SerialName("cdnsWithName") val cdnsWithName: List<PlayCDN> = emptyList(),
    @SerialName("multirates") val multiRates: List<PlayRate> = emptyList(),
)
