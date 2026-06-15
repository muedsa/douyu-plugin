package com.muedsa.tvbox.douyu.model

import kotlinx.serialization.Serializable

@Serializable
class DouyuResp<T> (
    val code: Int = -1,
    val data: T? = null,
)
