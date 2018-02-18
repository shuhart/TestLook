package com.shuhart.testlook.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

fun Context?.savePrefs(key: String, value: String) {
    this ?: return
    getDefaultSharedPrefs().edit {
        it.putString(key, value)
    }
}

fun Context?.readStringPrefs(key: String): String? {
    this ?: return null
    return getDefaultSharedPrefs().getString(key, null)
}

fun Context.getDefaultSharedPrefs(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(this.applicationContext)