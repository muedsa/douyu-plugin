package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveBetard(
    val room: RoomInfo = RoomInfo(),
    @SerialName("seo_info") val seoInfo: SeoInfo = SeoInfo(),
)
