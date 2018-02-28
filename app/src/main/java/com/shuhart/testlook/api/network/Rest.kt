package com.shuhart.testlook.api.network

import android.util.Log
import com.shuhart.testlook.BuildConfig
import com.shuhart.testlook.utils.GsonUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class Rest {
    private val ENDPOINT = "https://yasen.hotellook.com/"
    private val TIMEOUT_CONNECTION: Long = 30 // in seconds
    private val TIMEOUT_READ: Long = 30 // in seconds

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
                .sslSocketFactory(TLSSocketFactory(arrayOf(buildTrustManager())))
                .build()
    }

    private fun buildTrustManager(): X509TrustManager {
        try {
            val tm = if (BuildConfig.DEBUG) {
                DebugApiTrustManager()
            } else {
                val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                trustManagerFactory.init(null as KeyStore?)
                trustManagerFactory.trustManagers[0]
            }
            return tm as X509TrustManager
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, e.message, e)
            throw e
        }
    }

    fun create(): Api = RETROFIT.create(Api::class.java)
}