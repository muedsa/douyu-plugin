package com.muedsa.tvbox.demoplugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BangumiTag(
    @SerialName("name") val name: String,
    @SerialName("count") val count: Int,
    // total_cont
)
