package com.shuhart.testlook.modules.flight.search.airport

import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent
import com.shuhart.testlook.modules.base.Presenter
import io.reactivex.Observable

interface SearchAirportPresenter : Presenter {
    fun subscribeOnTextChanges(observable: Observable<TextViewAfterTextChangeEvent>)
}