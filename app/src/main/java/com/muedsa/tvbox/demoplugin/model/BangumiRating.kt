package com.muedsa.tvbox.demoplugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BangumiRating(
    @SerialName("total") var total: Int,
    @SerialName("count") var count: Map<String, Int> = emptyMap(),
    @SerialName("score") var score: Float,
)