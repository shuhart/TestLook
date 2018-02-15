package com.shuhart.testlook.api.model

import com.google.gson.annotations.SerializedName

data class City(
        @SerializedName("id")
        val id: Int,
        @SerializedName("fullname")
        val fullname: String,
        @SerializedName("iata")
        val iate: List<String>,
        @SerializedName("location")
        val location: Location) {

    override fun toString(): String = fullname
}