package com.grassyass.touchsomegrass.data.local

import android.app.Application
import android.content.Context

object Initialization {
    lateinit var application: Application

    fun isAppInitialized(): Boolean {
        val sharedPreferences = application.getSharedPreferences("Preferences", Context.MODE_PRIVATE)

        return sharedPreferences.getBoolean("isInitialized", false)
    }

    fun setAppAsInitialized() {
        val sharedPreferences = application.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean("isInitialized", true)
        editor.apply()
    }
}