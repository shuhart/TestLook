package com.shuhart.testlook.api.network

import com.shuhart.testlook.api.model.AutoCompleteResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("autocomplete")
    fun autocomplete(@Query("term") term: String,
                     @Query ("lang") lang: String): Single<Response<AutoCompleteResponse>>
}