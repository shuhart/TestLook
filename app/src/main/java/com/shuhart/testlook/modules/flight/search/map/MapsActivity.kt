package com.shuhart.testlook.modules.flight.search.map

import android.animation.ValueAnimator
import android.graphics.*
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.animation.AccelerateInterpolator
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
    private val circles = mutableListOf<Circle>()
    private lateinit var planeMarker: Marker
    private lateinit var planeBitmap: Bitmap

    private lateinit var animator: ValueAnimator
    private var previousZoom: Float = 0f
    private val maxProgress = 1000f

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

    private fun initPlaneMarker() {
        if (::planeMarker.isInitialized) return
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_plane)
        val origPoint = projection.toScreenLocation(originLatLng)
        val destPoint = projection.toScreenLocation(destLatLng)
        planeBitmap = if (origPoint.x < destPoint.x) {
            bitmap
        } else {
            flip(bitmap)
        }
    }

    private fun flip(source: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(1.0f, -1.0f)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setUpMap()
        addLocations()
        map.setOnCameraIdleListener {
            if (previousZoom != map.cameraPosition.zoom) {
                previousZoom = map.cameraPosition.zoom
                projection = map.projection
                initPlaneMarker()
                updatePath()
                startAnimation()
            }
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

        map.addMarker(MarkerOptions()
                .position(projection.fromScreenLocation(firstControlPoint))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        map.addMarker(MarkerOptions()
                .position(projection.fromScreenLocation(secondControlPoint))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))

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
        dots.clear()
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
        circles.forEach {
            it.remove()
        }
        val radius = getRadiusInMeters()
        dots.forEach {
            circles.add(map.addCircle(CircleOptions().center(it)
                    .fillColor(dotsColor)
                    .radius(radius)
                    .strokeWidth(0f)))
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

    private fun startAnimation() {
        if (::animator.isInitialized && animator.isRunning) return
        animator = ValueAnimator.ofFloat(0f, maxProgress)
                .setDuration(10000)
        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            val fraction = value / maxProgress
            animateTo(fraction)
        }
        animator.start()
    }

    private fun animateTo(progress: Float) {
        placePlaneMarker()
        val tan = FloatArray(2)
        val pos = FloatArray(2)
        pathMeasure.getPosTan(pathMeasure.length * progress, pos, tan)
        rotateAndPositionPlaneMarker(tan, pos)
    }

    private fun placePlaneMarker() {
        if (::planeMarker.isInitialized) return
        planeMarker = map.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(planeBitmap))
                .zIndex(2f)
                .anchor(0.5f, 0.5f)
                .position(originLatLng))
    }

    private fun rotateAndPositionPlaneMarker(tan: FloatArray, pos: FloatArray) {
        planeMarker.rotation = Math.toDegrees(Math.atan2(tan[1].toDouble(), tan[0].toDouble())).toFloat()
        val mapped = projection.fromScreenLocation(Point(pos[0].toInt(), pos[1].toInt()))
        if (mapped != null) {
            planeMarker.position = mapped
        }
    }

    override fun onDestroy() {
        if (this::animator.isInitialized) {
            animator.removeAllListeners()
            animator.cancel()
        }
        if (this::map.isInitialized) {
            map.clear()
        }
        super.onDestroy()
    }
}
