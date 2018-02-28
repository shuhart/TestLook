package com.shuhart.testlook.modules.base

import android.os.Bundle

interface Presenter {
    fun onDestroy()

    fun onCreate(bundle: Bundle?)

    fun onSaveInstanceState(bundle: Bundle)
}