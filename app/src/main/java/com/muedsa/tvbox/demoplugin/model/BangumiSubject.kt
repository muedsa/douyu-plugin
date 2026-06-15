package com.muedsa.tvbox.demoplugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BangumiSubject(
    @SerialName("id") val id: Long,
    @SerialName("type") val type: Int,
    @SerialName("name") val name: String,
    @SerialName("name_cn") val nameCn: String,
    @SerialName("summary") val summary: String,
    @SerialName("series") val series: Boolean,
    @SerialName("nsfw") val nsfw: Boolean,
    @SerialName("locked") val locked: Boolean,
    @SerialName("date") val date: String? = null,
    @SerialName("platform") val platform: String,
    @SerialName("images") val images: BangumiImages,
    // infobox
    @SerialName("volumes") val volumes: Int,
    @SerialName("eps") val eps: Int,
    // @SerialName("total_episodes") val totalEpisodes: Int,
    @SerialName("rating") val rating: BangumiRating? = null,
    // collection
    @SerialName("meta_tags") val metaTags: List<String> = emptyList(),
    @SerialName("tags") val tags: List<BangumiTag> = emptyList(),
)
