package com.grassyass.touchsomegrass.network.api

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.grassyass.touchsomegrass.models.Exercise
import com.grassyass.touchsomegrass.network.Authentication
import com.grassyass.touchsomegrass.network.Database

object ExercisesAPI {
    private val userUID: String? = Authentication.getCurrentUser()?.uid

    fun addExercise(exercise: Exercise) {
        Database.pushData("/exercises/$userUID", exercise)
    }

    fun deleteExercise(exerciseUID: String) {
        val updates = hashMapOf<String, Any>(
            "/exercises/$userUID/$exerciseUID" to { },
            "/exerciseSessions/$userUID/$exerciseUID" to { }
        )

        Database.updateChildren(updates)
    }

    fun getExercises(): Task<DataSnapshot> {
        return Database.readData("/exercises/$userUID")
    }
}