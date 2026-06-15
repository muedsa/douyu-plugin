package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category3ListRoomInfo(
    val type: Int = 0,
    val rid: Long = 0,
    val rn: String = "",
    val uid: Long = 0,
    val nn: String = "",
    val cid1: Int = 0,
    val cid2: Int = 0,
    @SerialName("cid2_display") val cid2Display: Int = 0,
    val cid3: Int = 0,
    val av: String = "",
    // val ol: Long = 0,
    val rs16: String = "",
)