package com.grassyass.touchsomegrass.network.api

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.grassyass.touchsomegrass.models.Session
import com.grassyass.touchsomegrass.network.Authentication
import com.grassyass.touchsomegrass.network.Database

object SessionsAPI {
    private val userUID: String? = Authentication.getCurrentUser()?.uid

    fun addSession(exerciseUID: String, session: Session): Task<Void> {
        return Database.pushData("/exerciseSessions/$userUID/$exerciseUID", session)
    }

    fun getSessions(exerciseUID: String): Task<DataSnapshot> {
        return Database.readData("/exerciseSessions/$userUID/$exerciseUID")
    }
}