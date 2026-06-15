package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
data class AvatarGroup(
    val big: String = "",
    val middle: String = "",
    val small: String = "",
)