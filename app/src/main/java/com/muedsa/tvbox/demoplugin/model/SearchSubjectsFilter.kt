package com.muedsa.tvbox.demoplugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchSubjectsFilter(
    @SerialName("type") var type: List<Int>? = null,
    @SerialName("meta_tags") var metaTags: List<String>? = null,
    @SerialName("tag") var tag: List<String>? = null,
    @SerialName("air_date") var airDate: List<String>? = null,
    @SerialName("rating") var rating: List<String>? = null,
    @SerialName("rank") var rank: List<String>? = null,
    @SerialName("nsfw") var nsfw: Boolean? = null,
)
