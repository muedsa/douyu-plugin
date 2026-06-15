package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.douyu.DouyuConst
import com.muedsa.tvbox.douyu.model.Category2RoomList
import com.muedsa.tvbox.douyu.model.CategoryList
import com.muedsa.tvbox.douyu.model.DouyuApiResp
import com.muedsa.tvbox.douyu.model.DouyuResp
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DouyuMobileService {

    @GET("api/cate/list")
    suspend fun categoryList(
        @Header("User-Agent") userAgent: String = DouyuConst.IOS_USER_AGENT,
        @Header("Referer") referer: String = DouyuConst.MOBILE_URL,
    ): DouyuResp<CategoryList>

    @GET("hgapi/live/cate/newRecList")
    suspend fun category2LiveList(
        @Query("cate2") cate2: String, // cate2 shortName
        @Query("offset") offset: Int = 1,
        @Query("limit") limit: Int = 20,
        @Header("User-Agent") userAgent: String = DouyuConst.IOS_USER_AGENT,
        @Header("Referer") referer: String = DouyuConst.MOBILE_URL,
    ): DouyuApiResp<Category2RoomList>
}