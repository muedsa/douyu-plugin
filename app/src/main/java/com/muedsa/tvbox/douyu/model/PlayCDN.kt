package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayCDN(
    val name: String = "",
    val cdn: String = "",
    val isH265: Boolean = false,
    @SerialName("re-weight") val reWeight: Int = 0,
)
