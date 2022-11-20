package com.grassyass.touchsomegrass.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.models.Exercise
import com.grassyass.touchsomegrass.network.api.ExercisesAPI

class ExerciseSettingsActivity : AppCompatActivity() {
    private lateinit var exercise: Exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_settings)

        exercise = intent.getSerializableExtra(getString(R.string.extra_edit_exercise)) as Exercise
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_exercise_actions_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete_exercise -> {
                ExercisesAPI.deleteExercise(exercise.id)
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}