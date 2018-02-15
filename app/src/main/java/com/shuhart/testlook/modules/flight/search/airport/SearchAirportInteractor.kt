package com.shuhart.testlook.modules.flight.search.airport

import com.shuhart.testlook.api.model.City
import com.shuhart.testlook.api.network.Rest
import io.reactivex.Single

class SearchAirportInteractor {
    fun autocomplete(term: String, lang: String): Single<List<City>> {
        return Rest.api.autocomplete(term, lang)
                .flatMap {
                    if (it.isSuccessful && it.body() != null) {
                        Single.just(it.body()!!.cities)
                    } else {
                        Single.error(Throwable())
                    }
                }
    }
}