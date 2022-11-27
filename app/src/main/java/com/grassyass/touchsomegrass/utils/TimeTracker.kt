package com.grassyass.touchsomegrass.utils

import android.os.Handler
import java.util.*

class TimeTracker: Tracker() {
    override fun start() {
        super.start()

        val handler = Handler()

        handler.postDelayed(object : Runnable {
            override fun run() {
                val now = Date().time
                data = now - startTime!!

                listener?.onValueChanged(data)

                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }
}