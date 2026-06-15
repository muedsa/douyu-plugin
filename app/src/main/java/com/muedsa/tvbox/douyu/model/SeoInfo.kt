package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeoInfo(
    @SerialName("seo_title") val seoTitle: String = "",
    @SerialName("seo_keyword") val seoKeyword: String = "",
    @SerialName("seo_description") val seoDescription: String = "",
)
