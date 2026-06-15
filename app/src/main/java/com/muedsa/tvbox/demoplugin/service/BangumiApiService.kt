package com.muedsa.tvbox.demoplugin.service

import com.muedsa.tvbox.demoplugin.model.BangumiEpisode
import com.muedsa.tvbox.demoplugin.model.BangumiFlow
import com.muedsa.tvbox.demoplugin.model.BangumiOfWeekday
import com.muedsa.tvbox.demoplugin.model.BangumiSubject
import com.muedsa.tvbox.demoplugin.model.SearchSubjectsParams
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BangumiApiService {

    @GET("calendar")
    suspend fun calendar(
        @Header("User-Agent") userAgent: String = FIXED_USER_AGENT,
    ): List<BangumiOfWeekday>

    @GET("v0/subjects")
    suspend fun subjects(
        @Query("type") type : Int = 2,
        @Query("cat") cat : Int? = null,
        @Query("sort") sort : String = "date",
        @Query("year") year : Int? = null,
        @Query("month") month : Int? = null,
        @Query("offset") offset : Int = 0,
        @Query("limit") limit : Int = 100,
        @Header("User-Agent") userAgent: String = FIXED_USER_AGENT,
    ): BangumiFlow<BangumiSubject>


    @GET("v0/subjects/{subjectId}")
    suspend fun subject(
        @Path("subjectId") subjectId: Long,
        @Header("User-Agent") userAgent: String = FIXED_USER_AGENT,
    ): BangumiSubject

    @GET("v0/episodes")
    suspend fun episodes(
        @Query("subject_id") subjectId: Long,
        @Query("type") type: Int? = null,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 100,
        @Header("User-Agent") userAgent: String = FIXED_USER_AGENT,
    ): BangumiFlow<BangumiEpisode>

    @POST("v0/search/subjects")
    suspend fun searchSubjects(
        @Body body: SearchSubjectsParams,
        @Query("offset") offset : Int = 0,
        @Query("limit") limit : Int = 20,
        @Header("User-Agent") userAgent: String = FIXED_USER_AGENT,
    ): BangumiFlow<BangumiSubject>

    companion object {
        const val FIXED_USER_AGENT = "muedsa/TvBoxDemoPlugin/0.0.1 (Android) (https://github.com/muedsa/TvBoxDemoPlugin)"
    }
}