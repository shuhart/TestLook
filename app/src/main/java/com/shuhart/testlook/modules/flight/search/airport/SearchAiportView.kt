package com.shuhart.testlook.modules.flight.search.airport

import com.shuhart.testlook.api.model.City

interface SearchAiportView {
    fun setItems(items: List<City>)
    fun getItems(): List<City>
    fun showToast(message: String?)
}