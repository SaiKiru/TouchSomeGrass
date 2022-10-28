package com.grassyass.touchsomegrass.activities

import android.content.Context
import android.content.Intent
import android.hardware.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.grassyass.touchsomegrass.R

class AppLockActivity : AppCompatActivity(), SensorEventListener {
    private var stepCount: Int = -1
    private var requiredCount: Int = 10
    private lateinit var stepCountTextView: TextView
    private lateinit var requiredCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_lock)

        stepCountTextView = findViewById(R.id.step_count_text_view)
        requiredCountTextView = findViewById(R.id.required_count_text_view)

        requiredCountTextView.text = "${requiredCount} steps"
    }

    override fun onStart() {
        super.onStart()

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent ?: return

        sensorEvent.values.firstOrNull()?.let {
            stepCount++

            if (stepCount >= requiredCount) {
                finish()
            } else {
                stepCountTextView.text = stepCount.toString()
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // empty code
    }

    override fun onBackPressed() {
        Intent(Intent.ACTION_MAIN).also {
            it.addCategory(Intent.CATEGORY_HOME)
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(it)
        }
    }
}