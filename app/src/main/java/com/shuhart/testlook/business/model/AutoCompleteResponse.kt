package com.shuhart.testlook.business.model

import com.google.gson.annotations.SerializedName

data class AutoCompleteResponse(
        @SerializedName("cities")
        val cities: List<City>)

data class City(
        @SerializedName("fullname")
        val fullname: String,
        @SerializedName("iata")
        val iate: List<String>,
        @SerializedName("location")
        val location: Location) {

    data class Location(
            @SerializedName("lat")
            val lat: Double,
            @SerializedName("lon")
            val lon: Double)
}