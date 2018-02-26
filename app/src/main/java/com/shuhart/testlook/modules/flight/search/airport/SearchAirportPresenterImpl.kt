package com.shuhart.testlook.modules.flight.search.airport

import android.content.Context
import android.util.Log
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent
import com.shuhart.testlook.R
import com.shuhart.testlook.modules.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit

class SearchAirportPresenterImpl(private val interactor: SearchAirportInteractor,
                                 private val view: SearchAirportView,
                                 private val context: Context) : BasePresenter(), SearchAirportPresenter {

    private var loadDisposable: Disposable? = null

    override fun subscribeOnTextChanges(observable: Observable<TextViewAfterTextChangeEvent>) {
        subscribe(observable
                .doOnNext { unsubscribe(loadDisposable) }
                .flatMap { Observable.just(it.editable()?.toString() ?: "") }
                .doOnNext { view.setItems(listOf()) }
                .filter { it.isNotEmpty() }
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loadSuggestions(it)
                }, {
                    view.hideProgress()
                    Log.e(javaClass.simpleName, it.message, it)
                }))
    }

    private fun loadSuggestions(term: String) {
        view.showProgress()
        loadDisposable = interactor.autocomplete(term, Locale.getDefault().language)
                .subscribe({
                    view.hideProgress()
                    view.setItems(it)
                }, {
                    view.hideProgress()
                    view.showToast(context.getString(R.string.error_server))
                })
        subscribe(loadDisposable)
    }
}