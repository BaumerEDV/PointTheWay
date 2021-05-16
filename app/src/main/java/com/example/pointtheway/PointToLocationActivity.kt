package com.example.pointtheway

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng

class PointToLocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_to_location)

        val targetPosition : LatLng? = intent?.extras?.getParcelable(SELECTED_POSITION_KEY)
        Log.d("Stefan", "targetPosition is $targetPosition")

        Toast.makeText(this, targetPosition?.toString(), Toast.LENGTH_LONG).show()
    }
}