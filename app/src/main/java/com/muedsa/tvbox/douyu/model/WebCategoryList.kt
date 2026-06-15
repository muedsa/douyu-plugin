package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
data class WebCategoryList(
    val cateList: List<WebCategoryListInfo> = emptyList(),
    val timestamp: Long = 0,
    val version: String = "",
)
