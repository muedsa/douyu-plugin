package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
data class WebCategoryInfo(
    val url: String = "",
    val name: String = "",
    val tagId: Int = 0,
)
