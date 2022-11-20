package com.grassyass.touchsomegrass.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepTracker(val context: Context): Tracker(), SensorEventListener {
    var stepCount = -1

    override fun start() {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)

        super.start()
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent ?: return

        sensorEvent.values.firstOrNull()?.let {
            stepCount++
            data = stepCount

            listener?.onValueChanged(stepCount)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) { }
}