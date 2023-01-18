package com.grassyass.touchsomegrass.activities

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.local.Whitelist
import com.grassyass.touchsomegrass.fragments.dialogs.OverlayPermissionRequestDialog
import com.grassyass.touchsomegrass.fragments.dialogs.OverlayPermissionRequestDialog.OverlayPermissionDialogRequestListener
import com.grassyass.touchsomegrass.fragments.HomeFragment
import com.grassyass.touchsomegrass.fragments.SettingsFragment
import com.grassyass.touchsomegrass.fragments.ExercisesFragment
import com.grassyass.touchsomegrass.fragments.dialogs.UsagePackageStatsPermissionRequestDialog
import com.grassyass.touchsomegrass.fragments.dialogs.UsagePackageStatsPermissionRequestDialog.UsagePackageStatsPermissionRequestDialogListener
import com.grassyass.touchsomegrass.services.ActivityWatcherService

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var homeScreen: Fragment
    private lateinit var exercisesScreen: Fragment
    private lateinit var profileScreen: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Whitelist.application = applicationContext as Application

        initializeUI()
        requestPermissions()

        createNotificationChannel()
        Intent(this, ActivityWatcherService::class.java).also { intent ->
            startForegroundService(intent)
        }
    }

    private fun initializeUI() {
        bottomNavigation = findViewById(R.id.bottom_navigation)

        homeScreen = HomeFragment()
        exercisesScreen = ExercisesFragment()
        profileScreen = SettingsFragment()

        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_switcher, homeScreen)
            add(R.id.fragment_switcher, exercisesScreen)
            add(R.id.fragment_switcher, profileScreen)
            hide(exercisesScreen)
            hide(profileScreen)
        }.commit()

        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_item_home_screen -> {
                    supportFragmentManager.beginTransaction()
                        .hide(exercisesScreen)
                        .hide(profileScreen)
                        .show(homeScreen)
                        .commit()
                    true
                }
                R.id.nav_item_exercises_screen -> {
                    supportFragmentManager.beginTransaction()
                        .hide(homeScreen)
                        .hide(profileScreen)
                        .show(exercisesScreen)
                        .commit()
                    true
                }
                R.id.nav_item_profile_screen -> {
                    supportFragmentManager.beginTransaction()
                        .show(profileScreen)
                        .hide(homeScreen)
                        .hide(exercisesScreen)
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun requestPermissions() {
        val requestPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
            if (!isGranted) {
                finish()
            }
        }

        val requestOverlay = registerForActivityResult(StartActivityForResult()) {
            if (!Settings.canDrawOverlays(this)) {
                finish()
            }
        }

        // request permission for activity recognition
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }

        // request permission for running foreground services
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                requestPermissionLauncher.launch(Manifest.permission.FOREGROUND_SERVICE)
            }
        }

        // request permission for getting usage stats
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()

        val stats: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_YEARLY,
            time - (365 * 24 * 60 * 60_000L),
            time
        )

        if (stats.isEmpty()) {
            UsagePackageStatsPermissionRequestDialog().apply {
                setOnDialogButtonClickListener(object: UsagePackageStatsPermissionRequestDialogListener {
                    override fun onDialogPositiveClick() {
                        Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).also { intent ->
                            startActivity(intent)
                        }
                    }

                    override fun onDialogNegativeClick() {
                        finish()
                    }
                })

                show(supportFragmentManager, "usage_stats_permission_request_dialog")
            }
        }

        // request for system alert window permission
        if (!Settings.canDrawOverlays(this)) {
            OverlayPermissionRequestDialog().apply {
                setOnDialogButtonClickListener(object : OverlayPermissionDialogRequestListener {
                    override fun onDialogPositiveClick() {
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${packageName}")
                        ).also { intent ->
                            requestOverlay.launch(intent)
                        }
                    }

                    override fun onDialogNegativeClick() {
                        finish()
                    }
                })

                show(supportFragmentManager, "overlay_permission_request_dialog")
            }
        }
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create the NotificationChannel
        val name = getString(R.string.tracker_channel_name)
        val descriptionText = getString(R.string.tracker_channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(getString(R.string.tracker_channel_id), name, importance)
        mChannel.description = descriptionText
        notificationManager.createNotificationChannel(mChannel)

        // AppWatcher
        val appWatcherNotificationChannel = NotificationChannel(
            getString(R.string.app_watcher_channel_id),
            getString(R.string.app_watcher_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        appWatcherNotificationChannel.description = getString(R.string.app_watcher_channel_description)
        notificationManager.createNotificationChannel(appWatcherNotificationChannel)

        // Inactivity Warning
        val inactivityWarningNotificationChannel = NotificationChannel(
            getString(R.string.warning_channel_id),
            getString(R.string.warning_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        inactivityWarningNotificationChannel.description = getString(R.string.warning_channel_id)
        notificationManager.createNotificationChannel(inactivityWarningNotificationChannel)
    }
}
