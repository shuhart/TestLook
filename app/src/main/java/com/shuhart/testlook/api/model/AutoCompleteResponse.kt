package com.shuhart.testlook.api.model

import com.google.gson.annotations.SerializedName

data class AutoCompleteResponse(
        @SerializedName("cities")
        val cities: List<City>)