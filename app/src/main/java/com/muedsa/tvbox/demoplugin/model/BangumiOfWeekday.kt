package com.muedsa.tvbox.demoplugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BangumiOfWeekday(
    @SerialName("weekday") var weekday: Weekday,
    @SerialName("items") var items: List<BangumiInfoOfCalendar> = listOf()
)