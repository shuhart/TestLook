package com.shuhart.testlook.api.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.shuhart.testlook.BuildConfig
import com.shuhart.testlook.utils.GsonUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Rest {
    private const val ENDPOINT = "https://yasen.hotellook.com/"
    private const val TIMEOUT_CONNECTION: Long = 30 // in seconds
    private const val TIMEOUT_READ: Long = 30 // in seconds

    private val RETROFIT = Retrofit.Builder()
            .baseUrl(ENDPOINT)
            .client(buildHttpClient())
            .addConverterFactory(GsonConverterFactory.create(GsonUtils.gson))
            .addCallAdapterFactory(RxThreadCallAdapter(Schedulers.io(), AndroidSchedulers.mainThread()))
            .build()

    private fun buildHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            if (BuildConfig.DEBUG) {
                Log.d(javaClass.simpleName, it)
            }
        })
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .build()
    }

    val api = RETROFIT.create(Api::class.java)
}