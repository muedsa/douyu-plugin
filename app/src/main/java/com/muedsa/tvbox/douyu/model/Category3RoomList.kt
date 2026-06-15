package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
data class Category3RoomList(
    val rl: List<Category3ListRoomInfo> = emptyList(),
    val userRecommendRec: Boolean = false,
)
