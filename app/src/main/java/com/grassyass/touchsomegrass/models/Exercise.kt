package com.grassyass.touchsomegrass.models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Exercise(
    val type: ExerciseType? = null,
    val name: String? = "",
) : Serializable {

    enum class ExerciseType {
        StepExercise,
        DurationExercise
    }
}