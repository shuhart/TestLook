package com.shuhart.testlook.modules.flight.search.airport

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent
import com.shuhart.testlook.R
import com.shuhart.testlook.modules.base.BasePresenter
import com.shuhart.testlook.utils.SingleExtras
import com.shuhart.testlook.utils.livedata.observeK
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit

class SearchAirportPresenterImpl(private val viewModel: SearchAirportViewModel,
                                 private val view: SearchAirportView,
                                 private val context: Context,
                                 private val lifecycleOwner: LifecycleOwner) : BasePresenter(), SearchAirportPresenter {

    private var lastTerm: String? = null

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        lastTerm = SingleExtras.readFromBundle(bundle)
        viewModel.viewState.observeK(lifecycleOwner) {
            when (it) {
                is SearchAirportResult.Success -> {
                    view.hideProgress()
                    view.setItems(it.cities)
                }
                SearchAirportResult.Error -> {
                    view.hideProgress()
                    view.showToast(context.getString(R.string.error_server))
                }
            }
        }
    }

    override fun subscribeOnTextChanges(observable: Observable<TextViewAfterTextChangeEvent>) {
        subscribe(observable
                .flatMap { Observable.just(it.editable()?.toString() ?: "") }
                .doOnNext {
                    if (lastTerm != it) {
                        viewModel.unsubscribe()
                    }
                    view.hideProgress()
                }
                .doOnNext { view.setItems(listOf()) }
                .debounce(1, TimeUnit.SECONDS)
                .filter { it.isNotEmpty() }
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
        lastTerm = term
        viewModel.autocomplete(term, Locale.getDefault().language)
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putAll(SingleExtras(lastTerm).toBundle())
    }
}