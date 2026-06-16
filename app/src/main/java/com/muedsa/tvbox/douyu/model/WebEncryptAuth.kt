package com.muedsa.tvbox.douyu.model

data class WebEncryptAuth(
    val key: EncryptionInfo,
    val algVer: String,
    val keyVer: String,
    val auth: String,
    val ts: Long,
)
