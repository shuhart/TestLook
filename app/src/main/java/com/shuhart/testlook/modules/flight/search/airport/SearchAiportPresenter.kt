package com.shuhart.testlook.modules.flight.search.airport

import android.util.Log
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent
import com.shuhart.testlook.api.model.City
import com.shuhart.testlook.modules.base.Presenter
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit

class SearchAiportPresenter(private val view: SearchAiportView) : Presenter() {
    private val interactor = SearchAirportInteractor()
    private var loadDisposable: Disposable? = null

    fun subscribeOnTextChanges(observable: Observable<TextViewAfterTextChangeEvent>) {
        subscribe(observable
                .flatMap { Observable.just(it.editable()?.toString() ?: "") }
                .doOnNext {
                    if (it.isEmpty()) {
                        view.setItems(listOf())
                    } else {
                        view.setItems(filterCitiesByNameOrIate(view.getItems(), it))
                    }
                }
                .filter { it.isNotEmpty() }
                .debounce(1, TimeUnit.SECONDS)
                .subscribe({
                    loadSuggestions(it)
                }, {
                    Log.e(javaClass.simpleName, it.message, it)
                }))
    }

    private fun filterCitiesByNameOrIate(items: List<City>, text: String): List<City> =
            view.getItems().filter {
                it.fullname.contains(text) || text.contains(it.fullname) ||
                        it.iate.any { it.contains(text) || text.contains(it) }
            }

    private fun loadSuggestions(term: String) {
        unsubscribe(loadDisposable)
        loadDisposable = interactor.autocomplete(term, Locale.getDefault().language)
                .subscribe({
                    view.setItems(it)
                }, {
                    view.showToast(it.message)
                })
        subscribe(loadDisposable)
    }
}