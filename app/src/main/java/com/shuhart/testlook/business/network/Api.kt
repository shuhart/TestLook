package com.shuhart.testlook.business.network

import com.shuhart.testlook.business.model.AutoCompleteResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("autocomplete")
    fun autocomplete(@Query("term") term: String,
                     @Query ("lang") lang: String): Single<Response<AutoCompleteResponse>>
}