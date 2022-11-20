package com.grassyass.touchsomegrass.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Exercise(
    val type: ExerciseType? = null,
    val name: String? = "",
) : Serializable {

    private var _id: String = ""

    var id: String
        @Exclude
        get() { return _id }
        set(value) { _id = value}

    enum class ExerciseType {
        StepExercise,
        DurationExercise
    }
}