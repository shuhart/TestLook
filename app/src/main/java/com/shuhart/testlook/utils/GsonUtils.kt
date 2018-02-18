package com.shuhart.testlook.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

object GsonUtils {
    val gson: Gson = GsonBuilder().create()

    fun <T> serialize(t: T?): String = t?.let { gson.toJson(it) } ?: ""

    inline fun <reified T> deserialize(json: String?): T? = deserialize(json, T::class.java)

    fun <T> deserialize(json: String?, type: Type?): T? {
        return try {
            gson.fromJson<T>(json, type)
        } catch (e: Throwable) {
            Log.e(javaClass.simpleName, e.message, e)
            null
        }
    }
}