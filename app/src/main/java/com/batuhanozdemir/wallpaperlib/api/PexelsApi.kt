package com.batuhanozdemir.wallpaperlib.api

import com.batuhanozdemir.wallpaperlib.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
val BASE_URL = "https://api.pexels.com/v1/"
private val API_KEY = "QBJVc8fNjHHjKM788aAMfihijGFhH27ZFnBh61R3U2ckUgCYsyFWaHPx"

interface PexelsApi {

    @GET("search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Header("Authorization") authorization: String = API_KEY
    ): SearchResponse

}