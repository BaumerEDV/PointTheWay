package com.example.pointtheway

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

const val SELECTED_POSITION_KEY = "com.example.pointtheway.POSITION_KEY"

class SelectLocationActivity : AppCompatActivity(), LocationListener {
    var selectedLocationMarker: Marker? = null
    var selectLocationButton: Button? = null
    val FINE_LOCATION_PERMISSION_REQUEST_CODE = 1
    var googleMap: GoogleMap? = null
    var isMapZoomedInAlready: Boolean = false
    var locationManager: LocationManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        val googleMapsFragment = supportFragmentManager.findFragmentById(R.id.shop_fragment)
        if (googleMapsFragment !is SupportMapFragment) {
            Log.e("Stefan", "The Map is a " + googleMapsFragment.toString())
            throw AssertionError("The Map is not what it's supposed to be!")
        }
        googleMapsFragment.getMapAsync {
            onMapReady(it)
        }

        val cancelButton: Button = findViewById(R.id.cancelLocationButton)
        cancelButton.setOnClickListener {
            val switchActivityIntent = Intent(this, MainActivity::class.java)
            //TODO: make this swap back to the previous activity instead
            startActivity(switchActivityIntent)
        }

        selectLocationButton = findViewById(R.id.confirmLocationButton)
        selectLocationButton?.setOnClickListener {
            val switchActivityIntent = Intent(this, PointToLocationActivity::class.java).apply {
                putExtra(SELECTED_POSITION_KEY, selectedLocationMarker?.position)
            }
            startActivity(switchActivityIntent)
        }

        Log.d("Stefan", "Second Activity onCreate finished")
    }

    private fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setOnMapClickListener { onMapClick(it, googleMap) }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true

            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, this)
        } else {
            Log.d("Stefan", "dont have location permission")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_LOCATION_PERMISSION_REQUEST_CODE
            )
        }


        Log.d("Stefan", "Map is ready!")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) {
            //permissions denied
            val switchActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(switchActivityIntent)
            return
        }
        when (requestCode) {
            FINE_LOCATION_PERMISSION_REQUEST_CODE -> {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    googleMap?.isMyLocationEnabled = true

                    locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, this)
                } else {
                    return
                }
            }
        }
    }


    private fun onMapClick(point: LatLng, googleMap: GoogleMap) {
        Log.d("Stefan", "Latitude: ${point.latitude}; Longitude: ${point.longitude}")

        if (selectedLocationMarker == null) {
            selectedLocationMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(point)
                    .title("Target Location")
                    .draggable(true)
            )
        } else {
            selectedLocationMarker?.position = point
        }
        selectLocationButton?.isEnabled = true
    }

    override fun onLocationChanged(location: Location) {
        if (isMapZoomedInAlready) {
            return
        }
        val cameraPosition =
            CameraPosition.Builder().target(LatLng(location.latitude, location.longitude))
                .zoom(17.0.toFloat()).build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        googleMap?.moveCamera(cameraUpdate)
        isMapZoomedInAlready = true
        locationManager?.removeUpdates(this)
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

}