package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayRate(
    val name: String = "",
    val rate: Int = -1,
    val highBit: Int = 0,
    val bit: Int = 0,
    val diamondFan: Int = 0,
)
