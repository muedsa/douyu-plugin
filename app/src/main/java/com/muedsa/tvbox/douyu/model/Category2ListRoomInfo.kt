package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
data class Category2ListRoomInfo(
    val rid: Long = 0,
    val roomName: String = "",
    val roomSrc: String = "",
    // rs_ext
    val nickname: String = "",
    val cate2Name: String = "",
    val cate2ShortName: String = "",
    val cate2ID: Int = 0,
    val avatar: String = "",
    val hn: String = "",
    val tag: String = "",
)
