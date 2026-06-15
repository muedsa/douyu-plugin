package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.douyu.DouyuConst
import com.muedsa.tvbox.douyu.model.Category3RoomList
import com.muedsa.tvbox.douyu.model.DouyuApiResp
import com.muedsa.tvbox.douyu.model.DouyuResp
import com.muedsa.tvbox.douyu.model.H5PlayInfo
import com.muedsa.tvbox.douyu.model.LiveBetard
import com.muedsa.tvbox.douyu.model.WebCategoryList
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DouyuService {
    @GET("gapi/rkc/directory/mixListV1/3_{category3Id}/{page}")
    suspend fun category3List(
        @Path("category3Id") category3Id: Int,
        @Path("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Header("User-Agent") userAgent: String = DouyuConst.IOS_USER_AGENT,
        @Header("Referer") referer: String = DouyuConst.PC_URL,
    ): DouyuResp<Category3RoomList>

    @GET("betard/{rid}")
    suspend fun roomBetard(
        @Path("rid") rid: Long,
        @Header("User-Agent") userAgent: String = DouyuConst.IOS_USER_AGENT,
        @Header("Referer") referer: String = "${DouyuConst.PC_URL}${rid}",
    ): LiveBetard

    @GET("japi/weblist/apinc/header/cate")
    suspend fun webCategoryList(
        @Header("User-Agent") userAgent: String = DouyuConst.IOS_USER_AGENT,
        @Header("Referer") referer: String = DouyuConst.PC_URL,
    ): DouyuApiResp<WebCategoryList>

    @GET("swf_api/homeH5Enc")
    suspend fun homeH5Enc(
        @Query("rids") rids: Long,
        @Header("User-Agent") userAgent: String = DouyuConst.IOS_USER_AGENT,
        @Header("Referer") referer: String = "${DouyuConst.PC_URL}${rids}",
    ): DouyuApiResp<Map<String, String>>

    @POST("lapi/live/getH5Play/{rid}")
    @FormUrlEncoded
    suspend fun getH5Play(
        @Path("rid") rid: Long,
        @FieldMap signParams: Map<String, String> = emptyMap(),
        @Field("cdn") cdn: String = "",
        @Field("rate") rate: Int = -1,
        @Field("ver") ver: String = "Douyu_223061205",
        @Field("iar") iar: Int = 1,
        @Field("ive") ive: Int = 1,
        @Field("hevc") hevc: Int = 0,
        @Field("fa") fa: Int = 0,
        @Header("User-Agent") userAgent: String = DouyuConst.IOS_USER_AGENT,
        @Header("Referer") referer: String = "${DouyuConst.PC_URL}${rid}",
    ): DouyuApiResp<H5PlayInfo>
}