package com.grassyass.touchsomegrass.data.network.api

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.grassyass.touchsomegrass.data.models.Exercise
import com.grassyass.touchsomegrass.data.models.User
import com.grassyass.touchsomegrass.data.network.Authentication
import com.grassyass.touchsomegrass.data.network.Database

object UsersAPI {
    private val userUID: String? = Authentication.getCurrentUser()?.uid

    fun addUser(user: User) {
        val defaultExercise = Exercise(
            Exercise.ExerciseType.StepExercise,
            "Walking",
            150
        )

        val updates = hashMapOf<String, Any>(
            "/users/$userUID" to user,
            "/exercises/$userUID/_default" to defaultExercise
        )

        Database.updateChildren(updates)
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

    fun addExp(amount: Double) {
        getUser().addOnSuccessListener {
            it.ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)!!

                    user.addExp(amount)

                    Database.writeData("/users/${userUID}/exp", user.exp!!)
                }

                override fun onCancelled(error: DatabaseError) { }
            })
        }
    }

    fun updateUser(user: User) {
        Database.writeData("/users/$userUID", user)
    }
}