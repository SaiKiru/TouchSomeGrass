package com.grassyass.touchsomegrass.data.network.api

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.grassyass.touchsomegrass.data.models.User
import com.grassyass.touchsomegrass.data.network.Authentication
import com.grassyass.touchsomegrass.data.network.Database

object UsersAPI {
    private val userUID: String? = Authentication.getCurrentUser()?.uid

    fun addUser(user: User) {
        Database.writeData("/users/$userUID", user)
    }

    fun deleteUser() {
        val emptyUser = User(null, null, null)

        val updates = hashMapOf<String, Any>(
            "/users/$userUID" to emptyUser,
            "/exercises/$userUID" to emptyUser,
            "/exerciseSessions/$userUID" to emptyUser
        )

        Database.updateChildren(updates)
    }

    fun getUser(): Task<DataSnapshot> {
        return Database.readData("/users/$userUID")
    }
}