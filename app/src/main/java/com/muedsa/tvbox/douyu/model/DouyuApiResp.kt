package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
class DouyuApiResp<T> (
    val error: Int = -1,
    val msg: String? = null,
    val data: T? = null,
)
