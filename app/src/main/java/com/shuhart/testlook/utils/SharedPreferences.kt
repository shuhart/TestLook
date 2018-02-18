package com.shuhart.testlook.utils

import android.content.SharedPreferences

inline fun SharedPreferences.edit(block: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    block(editor)
    editor.apply()
}