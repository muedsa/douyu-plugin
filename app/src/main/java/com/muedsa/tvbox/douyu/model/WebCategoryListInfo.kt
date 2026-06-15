package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
data class WebCategoryListInfo(
    val name: String = "",
    val list: List<WebCategoryInfo> = emptyList(),
)
