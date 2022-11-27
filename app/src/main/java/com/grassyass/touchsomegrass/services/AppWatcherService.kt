package com.grassyass.touchsomegrass.services

import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.grassyass.touchsomegrass.BuildConfig
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.activities.AppLockActivity
import com.grassyass.touchsomegrass.data.local.Whitelist
import java.util.*

class AppWatcherService : Service() {
    private var whitelist: ArrayList<String> = Whitelist.getAppList()
    private val criticalSystemApps: List<String> = listOf(
        "com.android.contacts",
        "com.android.settings",
        "com.android.vending"
    )
    private lateinit var handler: Handler
    private lateinit var appWatcherPoller: Runnable

    override fun onCreate() {
        super.onCreate()

        val launcherPackageName = packageManager.resolveActivity(
            Intent("android.intent.action.MAIN"),
            PackageManager.MATCH_DEFAULT_ONLY
        )?.activityInfo?.packageName

        var recentTasks: String
        val millisecondsPerMinute = 60_000L
        val pollingPeriod = 45 * millisecondsPerMinute
        val gracePeriod = 1 * millisecondsPerMinute

        handler = Handler()
        appWatcherPoller = object : Runnable {
            override fun run() {
                try {
                    var topPackageName = ""

                    val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                    val time = System.currentTimeMillis()

                    val stats: List<UsageStats> = usageStatsManager.queryUsageStats(
                        UsageStatsManager.INTERVAL_DAILY,
                        time - (pollingPeriod + gracePeriod),
                        time
                    )

                    val sortedMap = TreeMap<Long, UsageStats>()

                    for (usageStats in stats) {
                        sortedMap[usageStats.lastTimeUsed] = usageStats
                    }

                    if (sortedMap.isNotEmpty()) {
                        topPackageName = sortedMap.get(sortedMap.lastKey())!!.packageName
                    }

                    recentTasks = topPackageName

                    if (recentTasks.isNotEmpty()
                        && recentTasks != BuildConfig.APPLICATION_ID
                        && recentTasks != launcherPackageName
                        && !criticalSystemApps.contains(recentTasks)
                        && !whitelist.contains(recentTasks)
                    ) {
                        Intent(applicationContext, AppLockActivity::class.java).also {
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            startActivity(it)
                        }
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                handler.postDelayed(this, 3_000)
            }
        }

        handler.postDelayed(appWatcherPoller, 3_000)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        whitelist = Whitelist.getAppList()
        createPersistentNotification()

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(appWatcherPoller)
        stopForeground(true)
        stopSelf()
    }

    private fun createPersistentNotification() {
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, AppLockActivity::class.java), PendingIntent.FLAG_IMMUTABLE)

        startForeground(2, NotificationCompat.Builder(this, "AppWatcherNotification")
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Time to exercise!")
            .setContentText("Get moving now and touch some grass")
            .setContentIntent(pendingIntent)
            .build()
        )
    }
}