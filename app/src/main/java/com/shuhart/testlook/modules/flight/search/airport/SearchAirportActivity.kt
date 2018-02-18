package com.shuhart.testlook.modules.flight.search.airport

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import com.shuhart.testlook.R
import com.shuhart.testlook.api.model.City
import com.shuhart.testlook.utils.SingleExtras
import kotlinx.android.synthetic.main.activity_search_airport.*

class SearchAirportActivity : AppCompatActivity(), SearchAirportView {
    private lateinit var presenter: SearchAirportPresenter
    private lateinit var adapter: SearchAirportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SearchAirportPresenter(this, this)
        setContentView(R.layout.activity_search_airport)
        setup()
    }

    private fun setup() {
        adapter = SearchAirportAdapter()
        adapter.listener = object : SearchAirportAdapter.CityItemClickListener {
            override fun onCityItemClicked(city: City) {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtras(SingleExtras(city).toBundle())
                })
                finish()
            }
        }
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        presenter.subscribeOnTextChanges(RxTextView.afterTextChangeEvents(search_view))
    }

    override fun setItems(items: List<City>) {
        val old = adapter.items
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return old[oldItemPosition].id == items[newItemPosition].id
            }

            override fun getOldListSize(): Int = old.size

            override fun getNewListSize(): Int = items.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return old[oldItemPosition] == items[newItemPosition]
            }
        })
        adapter.items.clear()
        adapter.items.addAll(items)
        diffResult.dispatchUpdatesTo(adapter)
    }

    override fun getItems(): List<City> = adapter.items

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}