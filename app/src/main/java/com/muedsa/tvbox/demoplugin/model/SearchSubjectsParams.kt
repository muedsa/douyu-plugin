package com.muedsa.tvbox.demoplugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchSubjectsParams(
    @SerialName("keyword") val keyword: String,
    @SerialName("sort") val sort: String = "date",
    @SerialName("filter") val filter: SearchSubjectsFilter? = null,
)
