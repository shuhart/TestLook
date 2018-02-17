package com.shuhart.testlook.modules.flight.search.airport

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.util.ListUpdateCallback
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import com.shuhart.testlook.R
import com.shuhart.testlook.api.model.City
import com.shuhart.testlook.utils.SingleExtras

class SearchAirportActivity : AppCompatActivity(), SearchAirportView {
    private lateinit var presenter: SearchAirportPresenter
    private lateinit var adapter: SearchAirportAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = SearchAirportPresenter(this, this)
        setContentView(R.layout.activity_search_airport)
        setup()
    }

    private fun setup() {
        recyclerView = findViewById(R.id.recycler_view)
        adapter = SearchAirportAdapter()
        adapter.listener = object : SearchAirportAdapter.CityItemClickListener {
            override fun onCityItemClicked(city: City) {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtras(SingleExtras(city).toBundle())
                })
                finish()
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val searchView = findViewById<EditText>(R.id.search_view)
        presenter.subscribeOnTextChanges(RxTextView.afterTextChangeEvents(searchView))
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
        diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onChanged(position: Int, count: Int, payload: Any?) {
                Log.d(javaClass.simpleName, "onChanged($position, $count)")
                adapter.notifyItemRangeChanged(position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                Log.d(javaClass.simpleName, "onMoved($fromPosition, $toPosition)")
                adapter.notifyItemMoved(fromPosition, toPosition)
            }

            override fun onInserted(position: Int, count: Int) {
                Log.d(javaClass.simpleName, "onInserted($position, $count)")
                adapter.notifyItemRangeInserted(position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                Log.d(javaClass.simpleName, "onRemoved($position, $count)")
                adapter.notifyItemRangeRemoved(position, count)
            }

        })
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