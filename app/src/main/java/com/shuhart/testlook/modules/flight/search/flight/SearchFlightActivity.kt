package com.shuhart.testlook.modules.flight.search.flight

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shuhart.testlook.R
import com.shuhart.testlook.api.model.City
import com.shuhart.testlook.modules.flight.search.airport.SearchAirportActivity
import com.shuhart.testlook.utils.SingleExtras
import kotlinx.android.synthetic.main.activity_search_flight.*

class SearchFlightActivity : AppCompatActivity() {
    private val REQUEST_CODE_FROM = 1000
    private val REQUEST_CODE_TO = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_flight)
        from.setOnClickListener {
            startActivityForResult(Intent(this, SearchAirportActivity::class.java), REQUEST_CODE_FROM)
        }
        to.setOnClickListener {
            startActivityForResult(Intent(this, SearchAirportActivity::class.java), REQUEST_CODE_TO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        val city = SingleExtras.readFromBundle<City>(data?.extras) ?: return
        when (requestCode) {
            REQUEST_CODE_FROM -> {
                from.text = city.toString()
                from.tag = city
            }
            REQUEST_CODE_TO -> {
                to.text = to.toString()
                to.tag = city
            }
        }
    }
}