package com.grassyass.touchsomegrass.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
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
        val target = if (targetString.isEmpty()) 0L
                    else targetString.toLong()

        if (exerciseName == "") return

        when (trackerRadioGroup.checkedRadioButtonId) {
            R.id.tracker_option_movement -> {
                exerciseType = Exercise.ExerciseType.StepExercise
            }
            else -> {
                exerciseType = Exercise.ExerciseType.DurationExercise
            }
        }

        val exercise = Exercise(exerciseType, exerciseName)
        exercise.target = target

        val intent = Intent()
        intent.putExtra(getString(R.string.extra_new_exercise), exercise)

        setResult(RESULT_OK, intent)
        finish()
    }
}