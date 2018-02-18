package com.shuhart.testlook.modules.flight.search.map

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.shuhart.testlook.R
import com.shuhart.testlook.api.model.City
import com.shuhart.testlook.utils.PairExtras

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var origin: City
    private lateinit var dest: City
    private lateinit var originLatLng: LatLng
    private lateinit var destLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val (origin, dest) = PairExtras.readFromBundle(intent.extras, City::class.java, City::class.java)
        if (origin == null || dest == null) {
            error("Invalid extras.")
        }
        this.origin = origin
        this.dest = dest
        originLatLng = LatLng(origin.location.lat, origin.location.lon)
        destLatLng = LatLng(dest.location.lat, dest.location.lon)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        addLocations()
        zoomToFitLocations()
    }

    private fun addLocations() {
        map.addMarker(MarkerOptions().position(originLatLng).title(origin.name))
        map.addMarker(MarkerOptions().position(destLatLng).title(dest.name))
    }

    private fun zoomToFitLocations() {
        val builder = LatLngBounds.Builder()

        builder.include(originLatLng)
        builder.include(destLatLng)

        val bounds = builder.build()
        val dm = resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        val padding = (width * 0.20).toInt()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)

        map.moveCamera(cu)
    }
}
