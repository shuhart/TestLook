package com.shuhart.testlook.modules.flight.search.airport

import com.shuhart.testlook.api.model.City
import com.shuhart.testlook.api.network.Api
import io.reactivex.Single
import javax.inject.Inject

class SearchAirportInteractorImpl @Inject constructor(private val api: Api) : SearchAirportInteractor {
    override fun autocomplete(term: String, lang: String): Single<List<City>> {
        return api.autocomplete(term, lang)
                .flatMap {
                    if (it.isSuccessful && it.body() != null) {
                        Single.just(it.body()!!.cities)
                    } else {
                        Single.error(Throwable())
                    }
                }
    }
}