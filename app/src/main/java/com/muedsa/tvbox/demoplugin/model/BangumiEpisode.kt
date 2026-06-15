package com.muedsa.tvbox.demoplugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BangumiEpisode(
    @SerialName("id") var id: Long,
    @SerialName("subject_id") var subjectId: Long,
    @SerialName("name") var name: String,
    @SerialName("name_cn") var nameCn: String,
    @SerialName("desc") var desc: String,
    @SerialName("airdate") var airDate: String,
    @SerialName("ep") var ep: Int,
    @SerialName("comment") var comment: Int,
    @SerialName("type") var type: Int,
    @SerialName("disc") var disc: Int,
    @SerialName("duration") var duration: String,
    @SerialName("duration_seconds") var durationSeconds: Int,
)
