package com.grassyass.touchsomegrass.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.activities.MainActivity
import com.grassyass.touchsomegrass.data.models.Session
import com.grassyass.touchsomegrass.data.network.api.SessionsAPI
import com.grassyass.touchsomegrass.data.network.api.UsersAPI
import com.grassyass.touchsomegrass.utils.StepTracker

class ActivityWatcherService : Service() {
    private lateinit var tracker: StepTracker

    override fun onCreate() {
        super.onCreate()

        createPersistentNotification()

        val handler = Handler()
        val target = 100
        val millisecondsPerMinute = 60_000L
        val pollingPeriod = 45 * millisecondsPerMinute
        tracker = StepTracker(applicationContext)

        tracker.start()

        handler.postDelayed(object : Runnable {
            override fun run() {
                tracker.end()
                recordSession()

                if (tracker.data as Int <= target) {
                    Intent(applicationContext, AppWatcherService::class.java).also { intent ->
                        startService(intent)
                    }
                }

                tracker.reset()
                tracker.start()

                handler.postDelayed(this, pollingPeriod)
            }
        }, pollingPeriod)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private fun createPersistentNotification() {
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)

        startForeground(1 , NotificationCompat.Builder(this, getString(R.string.tracker_channel_id))
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Don't forget to exercise!")
            .setContentText("We're watching your every move \uD83D\uDC40") // \uD83D\UDC40 -> 👀
            .setContentIntent(pendingIntent)
            .build()
        )
    }

    private fun recordSession() {
        Session(
            tracker.startTime,
            tracker.endTime,
            tracker.data
        ).also { session ->
            SessionsAPI.addSession("_default", session)

            val suggestedStepCount = 17_500.0
            val heartPoints = 100.0
            val ratio = (tracker.data as Int) / suggestedStepCount

            val additionalExp = ratio * heartPoints

            UsersAPI.addExp(additionalExp)
        }
    }
}