package com.example.pointtheway

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng

class PointToLocationActivity : AppCompatActivity(), LocationListener,
    SensorEventListener {

    private lateinit var targetPosition: LatLng
    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager
    private lateinit var debugDisplay: TextView
    private lateinit var compassNeedle: ImageView

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private var angleRelativeToMagneticNorth: Double = 0.0

    private var headingAngle: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_to_location)

        debugDisplay = findViewById(R.id.debugDisplay)

        val selectLocationButton: Button = findViewById(R.id.selectTargetLocationButton)
        compassNeedle = findViewById(R.id.compassNeedle)

        selectLocationButton.setOnClickListener{
            val switchActivityIntent = Intent(this, SelectLocationActivity::class.java)
            startActivity(switchActivityIntent)
        }

        val position: LatLng? = intent?.extras?.getParcelable(SELECTED_POSITION_KEY)
        if (position == null) {
            val switchActivityIntent = Intent(this, SelectLocationActivity::class.java)
            startActivity(switchActivityIntent)
        } else {
            targetPosition = position
            Log.d("Stefan", "targetPosition is $targetPosition")
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, this)
        } else {
            Log.d("Stefan", "no location permission, third activity is swapping to first")
            val switchActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(switchActivityIntent)
        }

        Log.d("Stefan", "location manager instantiated ($locationManager), starting sensor manager now...")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        Log.d("Stefan", "sensor manager instantiated ($sensorManager)")

        Toast.makeText(this, targetPosition.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
            Log.d("StefanSensor", "Accelerometer update")
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
            Log.d("StefanSensor", "magnetic field update")
        }
        updateOrientationAngles()
    }

    fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        // "rotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        // "orientationAngles" now has up-to-date information.

        val angleRelativeToMagneticNorth = -orientationAngles[0] * 360 / (2 * 3.14159f) //West: 90, East -90, South +-179
        this.angleRelativeToMagneticNorth = turnAzimuthRotationInto360DegreeFormat(
            angleRelativeToMagneticNorth.toDouble()
        )



        //Toast.makeText(this, "Azimuth-Rotation: $rotation", Toast.LENGTH_SHORT).show()
        //Toast.makeText(this, "Azimuth: ${orientationAngles[0]}, Pitch: ${orientationAngles[1]}, Roll: ${orientationAngles[2]}", Toast.LENGTH_SHORT).show()
    }


    override fun onLocationChanged(location: Location) {
        val currentPoint = LatLng(location.latitude, location.longitude)
        headingAngle = angleFromCoordinate(currentPoint, targetPosition) //West: 90, East: 270

        updateHeadingUI()
    }

    fun updateHeadingUI(){
        debugDisplay.text = "Azimuth-Rotation: $angleRelativeToMagneticNorth;\n Heading: $headingAngle"
        compassNeedle.rotation = (angleRelativeToMagneticNorth - headingAngle).toFloat()
    }

    fun turnAzimuthRotationInto360DegreeFormat(rotation: Double): Double{
        if(rotation > 0){
            return rotation
        } else {
            val rotationAbs = Math.abs(rotation)
            return 180 + (180 - rotationAbs)
        }
    }

    fun angleFromCoordinate(point1: LatLng, point2: LatLng): Double{
        return angleFromCoordinate(point1.latitude, point1.longitude, point2.latitude, point2.longitude)
    }

    fun angleFromCoordinate(lat1: Double, long1: Double, lat2: Double, long2: Double): Double{
        val dLon = (long2 - long1)

        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon)

        var brng = Math.atan2(y, x)

        //brng = Math.degrees(brng)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        brng = 360 - brng // count degrees clockwise - remove to make counter-clockwise

        return brng
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }
}