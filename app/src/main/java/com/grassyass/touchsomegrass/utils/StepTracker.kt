package com.grassyass.touchsomegrass.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepTracker(val context: Context): Tracker(), SensorEventListener {
    var initialSteps: Float? = null

    override fun start() {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        data = 0

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)

        super.start()
    }

    override fun reset() {
        super.reset()

        initialSteps = null
        data = 0
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent ?: return

        sensorEvent.values.firstOrNull()?.let {
            if (initialSteps == null) {
                initialSteps = sensorEvent.values[0]
            } else {
                data = (sensorEvent.values[0] - initialSteps!!).toInt()
            }

            listener?.onValueChanged(data)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) { }
}