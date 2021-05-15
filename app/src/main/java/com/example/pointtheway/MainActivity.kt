package com.example.pointtheway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.constraintlayout.motion.widget.Debug

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val selectTargetLocationButton: Button = findViewById(R.id.selectTargetLocationButton)
        selectTargetLocationButton.setOnClickListener {
            selectTargetLocation()
        }



    }

    private fun selectTargetLocation() {
        Log.d("Stefan", "selectTargetLocation() executed")
    }


}