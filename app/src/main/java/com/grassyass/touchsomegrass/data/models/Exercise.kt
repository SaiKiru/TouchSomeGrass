package com.grassyass.touchsomegrass.data.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Exercise(
    val type: ExerciseType? = ExerciseType.StepExercise,
    val name: String? = "",
    var _target: Any? = null,
) : Serializable {

    private var _id: String = ""

    var id: String
        @Exclude
        get() { return _id }
        set(value) { _id = value}

    var target: Any?
        get() { return _target }
        set(value) {
            when(type) {
                ExerciseType.StepExercise -> {
                    value as Long
                    val minimumSteps = 100

                    if (value.toInt() < minimumSteps) {
                        _target = minimumSteps
                    } else {
                        _target = value
                    }
                }
                ExerciseType.DurationExercise -> {
                    value as Long
                    val minimumMinutes = 5L
                    val millisecondsPerMinute = 60_000
                    val minimumTime = minimumMinutes * millisecondsPerMinute

                    if (value < minimumTime) {
                        _target = minimumTime
                    } else {
                        _target = value
                    }
                }
                else -> {}
            }
        }

    enum class ExerciseType {
        StepExercise,
        DurationExercise
    }
}