package com.shuhart.testlook.modules.flight.search.map

import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.shuhart.testlook.R
import com.shuhart.testlook.api.model.City
import com.shuhart.testlook.utils.PairExtras


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var origin: City
    private lateinit var dest: City
    private lateinit var originLatLng: LatLng
    private lateinit var destLatLng: LatLng
    private lateinit var projection: Projection

    private lateinit var path: Path
    private lateinit var pathMeasure: PathMeasure

    private var dotsdiameter = 0
    private var dotsMargin = 0
    private var dotsColor = 0
    private val dots = mutableListOf<LatLng>()

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
        dotsdiameter = resources.getDimensionPixelSize(R.dimen.search_flight_animation_dots_radius)
        dotsMargin = resources.getDimensionPixelSize(R.dimen.search_flight_animation_dots_margin)
        dotsColor = ContextCompat.getColor(this, R.color.colorAccent)
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
        setUpMap()
        addLocations()
        map.setOnCameraIdleListener {
            // todo check last zoom level
            Log.d(javaClass.simpleName, "Idle listener")
            projection = map.projection
            updatePath()
        }
        zoomToFitLocations()
    }

    private fun updatePath() {
        makePath()
        makeDots()
        drawPath()
    }

    private fun setUpMap() {
        map.uiSettings.apply {
            isCompassEnabled = false
            isZoomControlsEnabled = false
            isRotateGesturesEnabled = false
            isTiltGesturesEnabled = false
            isMapToolbarEnabled = false
        }
    }

    private fun addLocations() {
        map.addMarker(MarkerOptions().position(originLatLng).title(origin.name))
        map.addMarker(MarkerOptions().position(destLatLng).title(dest.name))
    }

    private fun zoomToFitLocations() {
        val cameraUpdate = buildCameraUpdate()
        map.moveCamera(cameraUpdate)
    }

    private fun buildCameraUpdate(): CameraUpdate {
        val bounds = LatLngBounds.Builder()
                .include(originLatLng)
                .include(destLatLng)
                .build()
        val dm = resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        val padding = (width * 0.15).toInt()
        return CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
    }


    private fun makePath() {
        path = Path()
        val startPoint = projection.toScreenLocation(originLatLng)
        val endPoint = projection.toScreenLocation(destLatLng)
        path.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())

        val (firstControlPoint, secondControlPoint) = makeControlPoints(startPoint, endPoint)
        path.cubicTo(firstControlPoint.x.toFloat(), firstControlPoint.y.toFloat(),
                secondControlPoint.x.toFloat(), secondControlPoint.y.toFloat(),
                endPoint.x.toFloat(), endPoint.y.toFloat())
        pathMeasure = PathMeasure(path, false)
    }

    private fun makeControlPoints(startPoint: Point, endPoint: Point): Pair<Point, Point> {
        val firstControlPoint: Point
        val secondControlPoint: Point

        val axis = Math.max(Math.abs(startPoint.x - endPoint.x), Math.abs(startPoint.y - endPoint.y))
        val diameter = axis / 2
        if (startPoint.y < endPoint.y) {
            if (startPoint.x < endPoint.x) {
                firstControlPoint = Point(startPoint.x + diameter, startPoint.y + diameter)
                secondControlPoint = Point(endPoint.x - diameter, endPoint.y - diameter)
            } else {
                firstControlPoint = Point(startPoint.x - diameter, startPoint.y + diameter)
                secondControlPoint = Point(endPoint.x + diameter, endPoint.y - diameter)
            }
        } else if (startPoint.y > endPoint.y) {
            if (startPoint.x <= endPoint.x) {
                firstControlPoint = Point(startPoint.x + diameter, startPoint.y - diameter)
                secondControlPoint = Point(endPoint.x - diameter, endPoint.y + diameter)
            } else {
                firstControlPoint = Point(startPoint.x - diameter, startPoint.y - diameter)
                secondControlPoint = Point(endPoint.x + diameter, endPoint.y + diameter)
            }
        } else {
            firstControlPoint = Point(startPoint.x - diameter, startPoint.y + diameter)
            secondControlPoint = Point(endPoint.x + diameter, endPoint.y - diameter)
        }

        return Pair(firstControlPoint, secondControlPoint)
    }

    private fun makeDots() {
        var count = (pathMeasure.length / (dotsdiameter * 2 + dotsMargin)).toInt()
        if (count > 40) {
            count = 40
        }
        for (i in 0 until count) {
            val pos = FloatArray(2)
            val distance = pathMeasure.length / count * i + dotsdiameter + dotsMargin
            pathMeasure.getPosTan(distance, pos, null)
            val screenPoint = Point(pos[0].toInt(), pos[1].toInt())
            dots.add(projection.fromScreenLocation(screenPoint))
        }
    }

    private fun drawPath() {
        val radius = getRadiusInMeters()
        dots.forEach {
            map.addCircle(CircleOptions().center(it)
                    .fillColor(dotsColor)
                    .radius(radius)
                    .strokeWidth(0f))
        }
    }

    private fun getRadiusInMeters(): Double {
        val first = LatLng(0.0, 0.0)
        val firstPoint = projection.toScreenLocation(first)
        firstPoint.x += dotsdiameter
        val second = projection.fromScreenLocation(firstPoint)
        val diameter = FloatArray(1)
        Location.distanceBetween(first.latitude, first.longitude, second.latitude, second.longitude, diameter)
        return diameter[0].toDouble()
    }
}
