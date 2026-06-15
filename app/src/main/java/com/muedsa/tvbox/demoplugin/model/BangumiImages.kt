package com.muedsa.tvbox.demoplugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BangumiImages(
    @SerialName("large") var large: String,
    @SerialName("common") var common: String,
    @SerialName("medium") var medium: String,
    @SerialName("small") var small: String,
    @SerialName("grid") var grid: String,
)