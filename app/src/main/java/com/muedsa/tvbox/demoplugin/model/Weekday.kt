package com.muedsa.tvbox.demoplugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Weekday(
    @SerialName("en") var en: String,
    @SerialName("cn") var cn: String,
    @SerialName("ja") var ja: String,
    @SerialName("id") var id: Int,
)