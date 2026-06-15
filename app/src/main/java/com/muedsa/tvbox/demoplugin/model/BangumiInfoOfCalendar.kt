package com.muedsa.tvbox.demoplugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BangumiInfoOfCalendar(
    @SerialName("id") var id: Long,
    @SerialName("url") var url: String,
    @SerialName("type") var type: Int,
    @SerialName("name") var name: String,
    @SerialName("name_cn") var nameCn: String,
    @SerialName("summary") var summary: String,
    @SerialName("air_date") var airDate: String,
    @SerialName("air_weekday") var airWeekday: Int,
    @SerialName("rating") var rating: BangumiRating? = null,
    @SerialName("rank") var rank: Int? = null,
    @SerialName("images") var images: BangumiImages,
//    @SerialName("collection") var collection: Collection? = Collection()
)