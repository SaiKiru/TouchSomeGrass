package com.grassyass.touchsomegrass.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.models.Exercise
import com.grassyass.touchsomegrass.data.network.api.ExercisesAPI
import com.grassyass.touchsomegrass.fragments.dialogs.DeleteExerciseConfirmDialog
import com.grassyass.touchsomegrass.fragments.dialogs.DeleteExerciseConfirmDialog.DeleteExerciseConfirmDialogListener

class ExerciseSettingsActivity : AppCompatActivity() {
    private lateinit var exercise: Exercise
    private lateinit var exerciseNameField: EditText
    private lateinit var targetField: EditText
    private lateinit var targetLabel: TextView
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_settings)

        exercise = intent.getSerializableExtra(getString(R.string.extra_edit_exercise)) as Exercise

        exerciseNameField = findViewById(R.id.exercise_name_field)
        targetField = findViewById(R.id.target_field)
        targetLabel = findViewById(R.id.target_label)
        saveButton = findViewById(R.id.save_button)

        exerciseNameField.setText(exercise.name, TextView.BufferType.EDITABLE)

        when (exercise.type) {
            Exercise.ExerciseType.StepExercise -> {
                val steps = exercise.target

                targetField.setText(steps.toString(), TextView.BufferType.EDITABLE)
                targetLabel.text = "steps"
            }
            Exercise.ExerciseType.DurationExercise -> {
                val millisecondsPerMinute = 60_000L
                val minutes = exercise.target as Long / millisecondsPerMinute

                targetField.setText(minutes.toString(), TextView.BufferType.EDITABLE)
                targetLabel.text = "minutes"
            }
            else -> { }
        }

        saveButton.setOnClickListener { saveEdits() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_exercise_actions_menu, menu)

        if (exercise.id == "_default") {
            menu?.findItem(R.id.action_delete_exercise)?.apply {
                isVisible = false
                isEnabled = false
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete_exercise -> {
                DeleteExerciseConfirmDialog().apply {
                    setOnDialogButtonClickListener(object: DeleteExerciseConfirmDialogListener {
                        override fun onDialogPositiveClick() {
                            ExercisesAPI.deleteExercise(exercise.id)
                            finish()
                        }

                        override fun onDialogNegativeClick() { }
                    })

                    show(supportFragmentManager, "delete_exercise_confirm_dialog")
                }

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveEdits() {
        val exerciseName = exerciseNameField.text.toString()
        val targetString = targetField.text.toString()
        val target = if (targetString.isBlank()) 0L
                    else targetString.toLong()

        if (exerciseName.isBlank()) {
            Toast.makeText(this, "Name must not be empty", Toast.LENGTH_SHORT).show()
            return
        }

        exercise.name = exerciseName
        when (exercise.type) {
            Exercise.ExerciseType.StepExercise -> {
                exercise.target = target
            }
            Exercise.ExerciseType.DurationExercise -> {
                val millisecondsPerMinute = 60_000L
                exercise.target = target * millisecondsPerMinute
            }
            else -> { }
        }

        ExercisesAPI.updateExercise(exercise.id, exercise)
        finish()
    }
}