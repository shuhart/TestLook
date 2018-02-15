package com.shuhart.testlook.utils

import android.os.Bundle

class SingleExtras<out A>(private val value: A) {

    companion object {
        val key = "single_extras_key"

        inline fun <reified A> readFromBundle(bundle: Bundle?): A? {
            if (bundle == null || bundle.isEmpty) {
                return null
            }

            if (bundle.containsKey(key)) {
                return GsonUtils.deserialize(bundle.getString(key), A::class.java)
            }
            return null
        }
    }

    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(key, GsonUtils.serialize(value))
        return bundle
    }


    override fun toString(): String = "($value)"
}