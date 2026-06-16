package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EncryptionKeyInfo(
    val key: String = "",
    @SerialName("key_ver") val keyVer: String = ""
)
