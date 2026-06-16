package com.muedsa.tvbox.douyu.service

import com.muedsa.tvbox.douyu.DouyuConst
import com.muedsa.tvbox.douyu.model.Category3RoomList
import com.muedsa.tvbox.douyu.model.DouyuApiResp
import com.muedsa.tvbox.douyu.model.DouyuResp
import com.muedsa.tvbox.douyu.model.EncryptionInfo
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
        @Field("iar") iar: Int = 0,
        @Field("ive") ive: Int = 0,
        @Field("sov") sov: Int = 1,
        @Field("hevc") hevc: Int = 1,
        @Field("fa") fa: Int = 0,
        @Header("User-Agent") userAgent: String = DouyuConst.IOS_USER_AGENT,
        @Header("Referer") referer: String = "${DouyuConst.PC_URL}${rid}",
    ): DouyuApiResp<H5PlayInfo>

    @GET("wgapi/livenc/liveweb/websec/getEncryption")
    suspend fun getEncryption(
        @Query("did") did: String,
        @Header("User-Agent") userAgent: String = DouyuConst.IOS_USER_AGENT,
        @Header("Referer") referer: String = DouyuConst.PC_URL,
    ): DouyuApiResp<EncryptionInfo>

    @POST("lapi/live/getH5PlayV1/{rid}")
    @FormUrlEncoded
    suspend fun getH5PlayV1(
        @Path("rid") rid: Long,
        @FieldMap signParams: Map<String, String> = emptyMap(),
        @Field("cdn") cdn: String = "",
        @Field("rate") rate: Int = -1,
        @Field("ver") ver: String = "Douyu_new",
        /**
         * Intelligent Adaptive Rate 智能动态码率
         * iar=1：开启前端自动切清晰度（根据网速动态升降码率）
         * iar=0：关闭自动切流，仅固定用户选中清晰度
         */
        @Field("iar") iar: Int = 0,
        /**
         * Interval EVO 分段码率统计
         * ive=1：前端已观看 3 次以上，开启精细化码率自适应策略
         * ive=0：新用户 / 观看次数少，使用基础码率自适应
         * 作用：后端根据该标识返回不同档位清晰度备选列表
         */
        @Field("ive") ive: Int = 0,
        /**
         * Super Original Video 原画 / 超高清原画流
         * sov=1：播放器支持原画无损高码率流
         * sov=0：不请求原画，仅提供标清 / 高清 / 蓝光常规档位
         */
        @Field("sov") sov: Int = 1,
        /**
         * H.265 / HEVC 高效视频编码
         * hevc=1：支持硬解 H265，请求返回 HEVC 直播流
         * hevc=0：仅返回 H.264 流
         */
        @Field("hevc") hevc: Int = 1,
        @Field("fa") fa: Int = 0,
        @Header("User-Agent") userAgent: String = DouyuConst.IOS_USER_AGENT,
        @Header("Referer") referer: String = "${DouyuConst.PC_URL}${rid}",
    ): DouyuApiResp<H5PlayInfo>

}