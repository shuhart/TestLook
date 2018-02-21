package com.shuhart.testlook.modules.flight.search.airport

import com.shuhart.testlook.api.model.City
import io.reactivex.Single

interface SearchAirportInteractor {
    fun autocomplete(term: String, lang: String): Single<List<City>>
}