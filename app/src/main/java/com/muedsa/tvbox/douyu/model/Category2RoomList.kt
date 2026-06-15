package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
data class Category2RoomList(
    val total: Int = 0,
    val list: List<Category2ListRoomInfo>? = emptyList(),
)