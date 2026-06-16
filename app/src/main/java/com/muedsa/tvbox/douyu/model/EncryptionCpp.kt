package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
data class EncryptionCpp(
    val danmu: EncryptionKeyInfo = EncryptionKeyInfo(),
    val heartbeat: EncryptionKeyInfo = EncryptionKeyInfo(),
)
