package com.grassyass.touchsomegrass.network.api

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.grassyass.touchsomegrass.models.User
import com.grassyass.touchsomegrass.network.Authentication
import com.grassyass.touchsomegrass.network.Database

object UsersAPI {
    private val userUID: String? = Authentication.getCurrentUser()?.uid

    fun addUser(user: User) {
        Database.writeData("/users/$userUID", user)
    }

    fun deleteUser() {
        val updates = hashMapOf<String, Any>(
            "/users/$userUID" to { },
            "/exercises/$userUID" to { },
            "/exerciseSessions/$userUID" to { }
        )

        Database.updateChildren(updates)
    }

    fun getUser(): Task<DataSnapshot> {
        return Database.readData("/users/$userUID")
    }
}