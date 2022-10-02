package com.grassyass.touchsomegrass.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Exercise(
    val type: ExerciseType? = null,
    val name: String? = "",
)

enum class ExerciseType {
    StepExercise,
    DurationExercise
}