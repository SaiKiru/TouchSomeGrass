package com.grassyass.touchsomegrass.data.network.api

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.grassyass.touchsomegrass.data.models.Exercise
import com.grassyass.touchsomegrass.data.network.Authentication
import com.grassyass.touchsomegrass.data.network.Database

object ExercisesAPI {
    private val userUID: String? = Authentication.getCurrentUser()?.uid

    fun addExercise(exercise: Exercise) {
        Database.pushData("/exercises/$userUID", exercise)
    }

    fun deleteExercise(exerciseUID: String) {
        val emptyExercise = Exercise(null, null)

        val updates = hashMapOf<String, Any>(
            "/exercises/$userUID/$exerciseUID" to emptyExercise,
            "/exerciseSessions/$userUID/$exerciseUID" to emptyExercise
        )

        Database.updateChildren(updates)
    }

    fun getExercises(): Task<DataSnapshot> {
        return Database.readData("/exercises/$userUID")
    }

    fun updateExercise(exerciseUID: String, exercise: Exercise) {
        Database.writeData("/exercises/$userUID/$exerciseUID", exercise)
    }
}