package com.grassyass.touchsomegrass.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.models.Exercise

class NewExerciseActivity : AppCompatActivity() {
    private lateinit var exerciseNameField: EditText
    private lateinit var createExerciseButton: Button
    private lateinit var trackerRadioGroup: RadioGroup
    private lateinit var trackerOptionMovement: RadioButton
    private lateinit var trackerOptionDuration: RadioButton
    private lateinit var targetField: EditText
    private lateinit var targetLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_exercise)

        exerciseNameField = findViewById(R.id.exercise_name_field)
        createExerciseButton = findViewById(R.id.create_exercise_button)
        trackerRadioGroup = findViewById(R.id.tracker_radio_group)
        trackerOptionMovement = findViewById(R.id.tracker_option_movement)
        trackerOptionDuration = findViewById(R.id.tracker_option_duration)
        targetField = findViewById(R.id.target_field)
        targetLabel = findViewById(R.id.target_label)

        trackerOptionMovement.setOnClickListener { targetLabel.text = "steps" }
        trackerOptionDuration.setOnClickListener { targetLabel.text = "minutes" }

        createExerciseButton.setOnClickListener { onCreateExerciseButtonPressed() }
    }

    private fun onCreateExerciseButtonPressed() {
        val exerciseName: String = exerciseNameField.text.toString()
        val exerciseType: Exercise.ExerciseType
        val targetString = targetField.text.toString()
        var target = if (targetString.isEmpty()) 0L
                    else targetString.toLong()

        if (exerciseName.isBlank()) {
            Toast.makeText(this, "Name must not be empty", Toast.LENGTH_SHORT).show()
            return
        }

        when (trackerRadioGroup.checkedRadioButtonId) {
            R.id.tracker_option_movement -> {
                exerciseType = Exercise.ExerciseType.StepExercise
            }
            else -> {
                val millisecondsPerMinute = 60_000L
                exerciseType = Exercise.ExerciseType.DurationExercise
                target *= millisecondsPerMinute
            }
        }

        if (exerciseType == Exercise.ExerciseType.StepExercise
            && target < 100
        ) {
            Toast.makeText(this, "Too few! Minimum is 100 steps!", Toast.LENGTH_SHORT).show()
            return
        } else if (exerciseType == Exercise.ExerciseType.DurationExercise
            && target < 5L
        ) {
            Toast.makeText(this, "Too short! Minimum time is 5 minutes!", Toast.LENGTH_SHORT).show()
            return
        }

        val exercise = Exercise(exerciseType, exerciseName)
        exercise.target = target

        val intent = Intent()
        intent.putExtra(getString(R.string.extra_new_exercise), exercise)

        setResult(RESULT_OK, intent)
        finish()
    }
}