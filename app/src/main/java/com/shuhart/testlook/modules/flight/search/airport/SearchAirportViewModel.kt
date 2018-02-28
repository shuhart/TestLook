package com.shuhart.testlook.modules.flight.search.airport

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.ViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class SearchAirportViewModel @Inject constructor(private val interactor: SearchAirportInteractor) : ViewModel() {
    private val messages = BehaviorSubject.create<SearchAirportResult>()
    private var disposable: Disposable? = null

    val viewState: LiveData<SearchAirportResult> = LiveDataReactiveStreams.fromPublisher(
            messages.toFlowable(BackpressureStrategy.LATEST)
    )

    fun autocomplete(term: String, lang: String) {
        disposable = interactor.autocomplete(term, lang)
                .subscribe({
                    messages.onNext(SearchAirportResult.Success(term, it))
                }, {
                    messages.onNext(SearchAirportResult.Error)
                })
    }

    fun unsubscribe() {
        disposable?.dispose()
    }
}