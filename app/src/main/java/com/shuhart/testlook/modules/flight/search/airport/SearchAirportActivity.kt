package com.shuhart.testlook.modules.flight.search.airport

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.util.ListUpdateCallback
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import com.shuhart.testlook.R
import com.shuhart.testlook.api.model.City
import com.shuhart.testlook.utils.SingleExtras
import kotlinx.android.synthetic.main.activity_search_airport.*

class SearchAirportActivity : AppCompatActivity(), SearchAiportView {
    private val presenter = SearchAiportPresenter(this)
    private lateinit var adapter: SearchAiportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_airport)
        setup()
    }

    private fun setup() {
        adapter = SearchAiportAdapter()
        recycler_view.adapter = adapter
        adapter.listener = object : SearchAiportAdapter.CityItemClickListener {
            override fun onCityItemClicked(city: City) {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtras(SingleExtras(city).toBundle())
                })
                finish()
            }
        }
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

    override fun showToast(message: String?) {
        Toast.makeText(this, message ?: getString(R.string.error_server), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}