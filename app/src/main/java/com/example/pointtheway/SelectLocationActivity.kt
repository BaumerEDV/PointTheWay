package com.example.pointtheway

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

const val SELECTED_POSITION_KEY = "com.example.pointtheway.POSITION_KEY"

class SelectLocationActivity : AppCompatActivity() {
    var selectedLocationMarker: Marker? = null;
    var selectLocationButton: Button? = null;


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

        val cancelButton : Button = findViewById(R.id.cancelLocationButton)
        cancelButton.setOnClickListener{
            val switchActivityIntent = Intent(this, MainActivity::class.java)
            //TODO: make this swap back to the previous activity instead
            startActivity(switchActivityIntent)
        }

        selectLocationButton = findViewById(R.id.confirmLocationButton)
        selectLocationButton?.setOnClickListener{
            val switchActivityIntent = Intent(this, PointToLocationActivity::class.java).apply {
                putExtra(SELECTED_POSITION_KEY, selectedLocationMarker?.position)
                //Log.d("Stefan", "put extra ${selectedLocationMarker?.position}")
            }
            startActivity(switchActivityIntent)
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
        selectLocationButton?.isEnabled = true
    }
}