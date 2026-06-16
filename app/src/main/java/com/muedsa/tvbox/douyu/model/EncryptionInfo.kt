package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EncryptionInfo(
    val cpp: EncryptionCpp = EncryptionCpp(),
    @SerialName("enc_data") val encData: String = "",
    @SerialName("enc_time") val encTime: Int = 0,
    @SerialName("expire_at") val expireAt: Long = 0,
    @SerialName("is_special") val iSpecial: Int = 0,
    val key: String = "",
    @SerialName("rand_str") val randStr: String = "",
)
