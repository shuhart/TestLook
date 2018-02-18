package com.shuhart.testlook.modules.flight.search.flight

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shuhart.testlook.R
import com.shuhart.testlook.api.model.City
import com.shuhart.testlook.modules.flight.search.airport.SearchAirportActivity
import com.shuhart.testlook.modules.flight.search.map.MapsActivity
import com.shuhart.testlook.utils.*
import kotlinx.android.synthetic.main.activity_search_flight.*

class SearchFlightActivity : AppCompatActivity() {
    private val REQUEST_CODE_FROM = 1000
    private val REQUEST_CODE_TO = 1001
    private val ORIGIN_PREFS_KEY = "origin"
    private val DEST_PREFS_KEY = "dest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_flight)
        readLocalCache()
        from.setOnClickListener {
            startActivityForResult(Intent(this, SearchAirportActivity::class.java), REQUEST_CODE_FROM)
        }
        to.setOnClickListener {
            startActivityForResult(Intent(this, SearchAirportActivity::class.java), REQUEST_CODE_TO)
        }
        btn.setOnClickListener {
            if (from.tag != null && to.tag != null) {
                val origin = from.tag as City
                val dest = to.tag as City
                startActivity(Intent(this, MapsActivity::class.java).apply {
                    putExtras(PairExtras(origin, dest).toBundle())
                })
            }
        }
    }

    private fun readLocalCache() {
        val originJson = readStringPrefs(ORIGIN_PREFS_KEY) ?: return
        val origin = GsonUtils.deserialize<City>(originJson) ?: return
        setOrigin(origin)

        val destJson = readStringPrefs(DEST_PREFS_KEY) ?: return
        val dest = GsonUtils.deserialize<City>(destJson) ?: return
        setDest(dest)
    }

    private fun setOrigin(city: City) {
        from.text = city.toString()
        from.tag = city
    }

    private fun setDest(city: City) {
        to.text = city.toString()
        to.tag = city
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        val city = SingleExtras.readFromBundle<City>(data?.extras) ?: return
        when (requestCode) {
            REQUEST_CODE_FROM -> {
                setOrigin(city)
                savePrefs(ORIGIN_PREFS_KEY, GsonUtils.serialize(city))
            }
            REQUEST_CODE_TO -> {
                setDest(city)
                savePrefs(DEST_PREFS_KEY, GsonUtils.serialize(city))
            }
        }
    }
}