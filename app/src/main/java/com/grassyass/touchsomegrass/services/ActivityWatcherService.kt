package com.grassyass.touchsomegrass.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.activities.AppLockActivity
import com.grassyass.touchsomegrass.activities.MainActivity
import com.grassyass.touchsomegrass.data.models.Session
import com.grassyass.touchsomegrass.data.network.api.SessionsAPI
import com.grassyass.touchsomegrass.data.network.api.UsersAPI
import com.grassyass.touchsomegrass.utils.StepTracker

class ActivityWatcherService : Service() {
    private lateinit var tracker: StepTracker

    override fun onCreate() {
        createPersistentNotification()

        super.onCreate()

        val handler = Handler()
        val target = 100
        val millisecondsPerMinute = 60_000L
        val pollingPeriod = 45 * millisecondsPerMinute
        val gracePeriod = 10 * millisecondsPerMinute
        var state = "DEFAULT"
        tracker = StepTracker(applicationContext)

        tracker.start()

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (state == "DEFAULT") {
                    if (tracker.data as Int <= target) {
                        showInactivityWarningNotification()
                    }

                    state = "GRACE_PERIOD"
                    handler.postDelayed(this, gracePeriod)
                } else if (state == "GRACE_PERIOD") {
                    tracker.end()
                    recordSession()

                    if (tracker.data as Int <= target) {
                        Intent(applicationContext, AppWatcherService::class.java).also { intent ->
                            startService(intent)
                        }
                    }

                    tracker.reset()
                    tracker.start()

                    state = "DEFAULT"
                    handler.postDelayed(this, pollingPeriod - gracePeriod)
                }
            }
        }, pollingPeriod - gracePeriod)
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

    private fun showInactivityWarningNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent = PendingIntent.getActivity(this, 3, Intent(Intent(this, AppLockActivity::class.java)), PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, getString(R.string.warning_channel_id))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Inactivity warning")
            .setContentText("You've been inactive for a while. Your phone will soon be locked")
            .setContentIntent(intent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(3, notification)
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