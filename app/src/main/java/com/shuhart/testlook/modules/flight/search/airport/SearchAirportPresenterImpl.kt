package com.shuhart.testlook.modules.flight.search.airport

import android.content.Context
import android.util.Log
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent
import com.shuhart.testlook.R
import com.shuhart.testlook.modules.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchAirportPresenterImpl() : BasePresenter(), SearchAirportPresenter {
    private lateinit var interactor: SearchAirportInteractor
    private lateinit var view: SearchAirportView
    private lateinit var context: Context

    @Inject
    constructor(interactor: SearchAirportInteractor,
                view: SearchAirportView,
                context: Context) : this() {
        this.interactor = interactor
        this.view = view
        this.context = context
    }

    private var loadDisposable: Disposable? = null

    override fun subscribeOnTextChanges(observable: Observable<TextViewAfterTextChangeEvent>) {
        subscribe(observable
                .flatMap { Observable.just(it.editable()?.toString() ?: "") }
                .doOnNext { view.setItems(listOf()) }
                .filter { it.isNotEmpty() }
                .debounce(1, TimeUnit.SECONDS)
                .subscribe({
                    loadSuggestions(it)
                }, {
                    Log.e(javaClass.simpleName, it.message, it)
                }))
    }

    private fun loadSuggestions(term: String) {
        unsubscribe(loadDisposable)
        loadDisposable = interactor.autocomplete(term, Locale.getDefault().language)
                .subscribe({
                    view.setItems(it)
                }, {
                    view.showToast(context.getString(R.string.error_server))
                })
        subscribe(loadDisposable)
    }
}