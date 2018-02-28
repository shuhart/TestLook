package com.shuhart.testlook.modules.flight.search.airport

import com.shuhart.testlook.api.model.City

sealed class SearchAirportResult {
    object Error : SearchAirportResult()
    data class Success(val term: String, val cities: List<City>) : SearchAirportResult()
}