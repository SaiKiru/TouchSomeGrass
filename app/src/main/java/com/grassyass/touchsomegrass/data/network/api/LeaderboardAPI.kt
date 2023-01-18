package com.grassyass.touchsomegrass.data.network.api

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.grassyass.touchsomegrass.data.network.Authentication
import com.grassyass.touchsomegrass.data.network.Database

object LeaderboardAPI {
    private val userUID: String? = Authentication.getCurrentUser()?.uid

    fun getGlobalRankings(): Task<DataSnapshot> {
        return Database.readData("/users")
    }
}