package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
data class Category2Info(
    val cate1Id: Int = 0,
    val cate2Id: Int = 0,
    val cate2Name: String = "",
    val shortName: String = "",
    val pic: String = "",
    val icon: String = "",
    val smallIcon: String = "",
    val count: Int = 0,
    val isVertical: Int = 0,
)