package com.grassyass.touchsomegrass.data.local

import android.app.Application
import android.content.Context

object Whitelist {
    lateinit var application: Application
    private val appList: ArrayList<String> = arrayListOf()

    fun getAppList(): ArrayList<String> {
        val sharedPreferences = application.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val savedApps = sharedPreferences.getStringSet("Whitelist", mutableSetOf())

        appList.clear()

        savedApps?.forEach { app ->
            appList.add(app)
        }

        return appList
    }

    fun removeApp(packageName: String) {
        val sharedPreferences = application.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        appList.remove(packageName)

        editor.putStringSet("Whitelist", appList.toSet())
        editor.apply()
    }

    fun addApp(packageName: String) {
        val sharedPreferences = application.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        appList.add(packageName)

        editor.putStringSet("Whitelist", appList.toSet())
        editor.apply()
    }
}