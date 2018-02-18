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

class PairExtras<out A, out B>(val first: A?, val second: B?) {

    companion object {
        private val firstKey = "pair_extras_first_key"
        private val secondKey = "pair_extras_second_key"

        fun <A, B> readFromBundle(bundle: Bundle?, classA: Class<A>?, classB: Class<B>?): PairExtras<A, B> {
            if (bundle == null || bundle.isEmpty) {
                return PairExtras(null, null)
            }

            var a: A? = null
            var b: B? = null

            if (bundle.containsKey(firstKey) && classA != null) {
                a = GsonUtils.deserialize(bundle.getString(firstKey), classA)
            }

            if (bundle.containsKey(secondKey) && classB != null) {
                b = GsonUtils.deserialize(bundle.getString(secondKey), classB)
            }
            return PairExtras(a, b)
        }
    }

    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString(firstKey, GsonUtils.serialize(first))
        bundle.putString(secondKey, GsonUtils.serialize(second))
        return bundle
    }

    operator fun component1() = first

    operator fun component2() = second


    override fun toString(): String = "($first, $second)"
}