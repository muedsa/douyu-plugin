package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 */
@Serializable
data class RoomInfo(
    @SerialName("show_status") val showStatus: Int = 0, // 1开播
    @SerialName("cate2_id") val cate2Id: String = "",
    val avatar: AvatarGroup = AvatarGroup(),
    @SerialName("chat_group") val chatGroup: Boolean = false,
    @SerialName("show_id") val showId: Long = 0,
    @SerialName("owner_uid") val ownerUid: Long = 0,
    @SerialName("nickname") val nickname: String = "",
    @SerialName("room_src") val roomSrc: String = "", // 相对路径
    @SerialName("cate3_id") val cate3Id: String = "",
    @SerialName("cate1_id") val cate1Id: String = "",
    @SerialName("room_id") val roomId: Long = -1,
    val status: String = "",
    @SerialName("room_name") val roomName: String = "",
    val rs1: String = "",  // 相对路径
    @SerialName("room_pic") val roomPic: String = "",
    @SerialName("owner_name") val ownerName: String = "",
    @SerialName("room_url") val roomUrl: String = "",
    val isVertical: Int = 0,
    val defaultSrc: String = "",
    val coverSrc: String = "",
)
