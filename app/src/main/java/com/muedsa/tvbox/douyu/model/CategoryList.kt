package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryList(
    val cate1Info: List<Category1Info> = emptyList(),
    val cate2Info: List<Category2Info> = emptyList(),
)
