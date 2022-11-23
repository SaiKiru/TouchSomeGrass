package com.grassyass.touchsomegrass.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.models.Exercise
import com.grassyass.touchsomegrass.data.network.api.ExercisesAPI
import com.grassyass.touchsomegrass.fragments.dialogs.DeleteExerciseConfirmDialog
import com.grassyass.touchsomegrass.fragments.dialogs.DeleteExerciseConfirmDialog.DeleteExerciseConfirmDialogListener

class ExerciseSettingsActivity : AppCompatActivity() {
    private lateinit var exercise: Exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_settings)

        exercise = intent.getSerializableExtra(getString(R.string.extra_edit_exercise)) as Exercise
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
}