package com.grassyass.touchsomegrass.services

import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.grassyass.touchsomegrass.BuildConfig
import com.grassyass.touchsomegrass.activities.AppLockActivity
import java.util.*

class AppWatcherService : Service() {

    override fun onCreate() {
        super.onCreate()

        val handler = Handler()
        var prevTasks: String
        var recentTasks = ""

        prevTasks = recentTasks

        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    var topPackageName = ""

                    val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                    val time = System.currentTimeMillis()

                    val stats: List<UsageStats> = usageStatsManager.queryUsageStats(
                        UsageStatsManager.INTERVAL_DAILY,
                        time - 5_000,
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

                    if (recentTasks.isNotEmpty() && recentTasks != prevTasks) {
                        if (recentTasks != BuildConfig.APPLICATION_ID) {
                            Intent(applicationContext, AppLockActivity::class.java).also {
                                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                startActivity(it)
                            }
                        }
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                prevTasks = recentTasks

                handler.postDelayed(this, 5_000)
            }
        }, 5_000)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}