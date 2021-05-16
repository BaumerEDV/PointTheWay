package com.example.pointtheway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class SelectLocationActivity : AppCompatActivity() {
    var selectedLocationMarker: Marker? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        val googleMapsFragment = supportFragmentManager.findFragmentById(R.id.shop_fragment)
        if (googleMapsFragment !is SupportMapFragment){
            Log.e("Stefan", "The Map is a " + googleMapsFragment.toString())
            throw AssertionError("The Map is not what it's supposed to be!")
        }
        googleMapsFragment.getMapAsync{
            onMapReady(it)
        }

        Log.d("Stefan", "Second Activity onCreate finished")
    }

    private fun onMapReady(googleMap: GoogleMap) {
        Log.d("Stefan", "Map is ready!")
        googleMap.setOnMapClickListener { onMapClick(it, googleMap) }
    }

    private fun onMapClick(point: LatLng, googleMap: GoogleMap){
        Log.d("Stefan", "Latitude: ${point.latitude}; Longitude: ${point.longitude}")

        if(selectedLocationMarker == null){
            selectedLocationMarker = googleMap.addMarker(MarkerOptions()
                .position(point)
                .title("Target Location")
                .draggable(true)
            )
        } else {
            selectedLocationMarker?.position = point
        }
    }
}