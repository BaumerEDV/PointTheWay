package com.example.pointtheway

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class SelectLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        val fragment = supportFragmentManager.findFragmentById(R.id.shop_fragment)
        if (fragment !is SupportMapFragment){
            Log.e("Stefan", "The Map is a " + fragment.toString())
            throw AssertionError("The Map is not what it's supposed to be!")
        }
        fragment.getMapAsync{
            onMapReady(it)
        }

        Log.d("Stefan", "Second Activity onCreate finished")
    }

    private fun onMapReady(googleMap: GoogleMap) {
        Log.d("Stefan", "Map is ready!")
        googleMap.setOnMapClickListener { onMapClick(it) }
    }

    private fun onMapClick(point: LatLng){
        Log.d("Stefan", "Latitude: ${point.latitude}; Longitude: ${point.longitude}")
    }
}