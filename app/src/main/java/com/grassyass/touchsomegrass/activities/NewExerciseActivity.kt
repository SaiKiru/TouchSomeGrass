package com.grassyass.touchsomegrass.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.grassyass.touchsomegrass.R

class NewExerciseActivity : AppCompatActivity() {
    private lateinit var exerciseNameField: EditText
    private lateinit var createExerciseButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_exercise)

        exerciseNameField = findViewById(R.id.exercise_name_field)
        createExerciseButton = findViewById(R.id.create_exercise_button)

        createExerciseButton.setOnClickListener { onCreateExerciseButtonPressed() }
    }

    private fun onCreateExerciseButtonPressed() {
        val exerciseName: String = exerciseNameField.text.toString()

        if (exerciseName == "") return

        val intent = Intent()
        intent.putExtra("Message", exerciseName)

        setResult(0, intent)
        finish()
    }
}