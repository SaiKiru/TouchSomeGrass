package com.grassyass.touchsomegrass.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.models.Exercise
import com.grassyass.touchsomegrass.data.models.Session
import com.grassyass.touchsomegrass.data.network.api.ExercisesAPI
import com.grassyass.touchsomegrass.data.network.api.SessionsAPI
import com.grassyass.touchsomegrass.utils.StepTracker
import com.grassyass.touchsomegrass.utils.Tracker

class AppLockActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var statusText: TextView
    private lateinit var progressValueTextView: TextView
    private lateinit var progressValueLabel: TextView
    private lateinit var targetLabel: TextView
    private lateinit var sessionControllerButton: Button
    private lateinit var exerciseListSpinner: Spinner
    private lateinit var activeExercise: Exercise
    private lateinit var tracker: Tracker
    private var exerciseList: ArrayList<Exercise> = arrayListOf()
    private var exerciseNamesList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_lock)

        statusText = findViewById(R.id.status_text)
        progressValueTextView = findViewById(R.id.progress_value_text_view)
        progressValueLabel = findViewById(R.id.progress_value_label)
        targetLabel = findViewById(R.id.target_label)
        sessionControllerButton = findViewById(R.id.session_controller_button)
        exerciseListSpinner = findViewById(R.id.exercise_list_spinner)

        populateExerciseListSpinner()

        sessionControllerButton.setOnClickListener {
            // temporarily disable session controller
            sessionControllerButton.text = "Stop Exercise"
            sessionControllerButton.isEnabled = false

            // mute less important data
            statusText.alpha = 0.3f
            targetLabel.alpha = 0.5f
            sessionControllerButton.alpha = 0.3f

            // hide exercise changer and disable changing exercises
            exerciseListSpinner.alpha = 0.0f
            exerciseListSpinner.isEnabled = false
            findViewById<TextView>(R.id.change_exercise_label).alpha = 0.0f

            startSession()
        }
    }

    override fun onBackPressed() {
        Intent(Intent.ACTION_MAIN).also {
            it.addCategory(Intent.CATEGORY_HOME)
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(it)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        activeExercise = exerciseList[pos]

        when(activeExercise.type) {
            Exercise.ExerciseType.StepExercise -> {
                targetLabel.text = "You need to take at least\n${activeExercise.target} steps"
                progressValueLabel.text = "steps done"
            }
            Exercise.ExerciseType.DurationExercise -> {
                // TODO: format time to be more human readable
                targetLabel.text = "You need to last for at least\n${activeExercise.target} ms"
                progressValueLabel.text = "time elapsed"
            }
            else -> { }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) { }

    private fun populateExerciseListSpinner() {
        ExercisesAPI.getExercises().addOnSuccessListener {
            it.ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { exerciseSnapshot ->
                        val exercise = exerciseSnapshot.getValue(Exercise::class.java)!!
                        exercise.id = exerciseSnapshot.key!!

                        exerciseList.add(exercise)
                        exerciseNamesList.add(exercise.name!!)
                    }

                    val exerciseListAdapter = ArrayAdapter(this@AppLockActivity, android.R.layout.simple_spinner_item, exerciseNamesList)
                    exerciseListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    exerciseListSpinner.adapter = exerciseListAdapter
                    exerciseListSpinner.onItemSelectedListener = this@AppLockActivity
                }

                override fun onCancelled(error: DatabaseError) { }
            })
        }
    }

    private fun initializeTracker() {
        when(activeExercise.type) {
            Exercise.ExerciseType.StepExercise -> {
                tracker = StepTracker(this)
                tracker.setOnValueChangedListener { value ->
                    value as Int
                    val target = activeExercise.target as Long

                    progressValueTextView.text = value.toString()

                    if (value >= target.toInt()) {
                        // notify user that exercise has been completed
                        statusText.text = "Yay!\nYou may now use the app!"
                        statusText.alpha = 1f

                        // set session controller to end session on click
                        sessionControllerButton.setOnClickListener {
                            endSession()
                        }

                        // re-enable session controller
                        sessionControllerButton.alpha = 1.0f
                        sessionControllerButton.isEnabled = true
                    }
                }
            }
            Exercise.ExerciseType.DurationExercise -> {
                // TODO: define logic for time tracking
            }
            else -> {}
        }
    }

    private fun startSession() {
        initializeTracker()
        tracker.start()
    }

    private fun endSession() {
        tracker.end()

        recordSession().addOnSuccessListener {
            finish()
        }
    }

    private fun recordSession(): Task<Void> {
        Session(
            tracker.startTime,
            tracker.endTime,
            tracker.data
        ).also { session ->
            return SessionsAPI.addSession(activeExercise.id, session)
        }
    }
}