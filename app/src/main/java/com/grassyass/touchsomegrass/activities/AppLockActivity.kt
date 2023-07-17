package com.grassyass.touchsomegrass.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.models.Exercise
import com.grassyass.touchsomegrass.data.models.Session
import com.grassyass.touchsomegrass.data.network.api.ExercisesAPI
import com.grassyass.touchsomegrass.data.network.api.SessionsAPI
import com.grassyass.touchsomegrass.data.network.api.UsersAPI
import com.grassyass.touchsomegrass.services.AppWatcherService
import com.grassyass.touchsomegrass.utils.StepTracker
import com.grassyass.touchsomegrass.utils.Time
import com.grassyass.touchsomegrass.utils.TimeTracker
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
    private lateinit var shader: FrameLayout
    private var exerciseList: ArrayList<Exercise> = arrayListOf()
    private var exerciseNamesList: ArrayList<String> = arrayListOf()

    private var isCasual = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_lock)

        statusText = findViewById(R.id.status_text)
        progressValueTextView = findViewById(R.id.progress_value_text_view)
        progressValueLabel = findViewById(R.id.progress_value_label)
        targetLabel = findViewById(R.id.target_label)
        sessionControllerButton = findViewById(R.id.session_controller_button)
        exerciseListSpinner = findViewById(R.id.exercise_list_spinner)
        shader = findViewById(R.id.shader)

        isCasual = intent.getBooleanExtra("isCasual", false)

        populateExerciseListSpinner()

        sessionControllerButton.setOnClickListener {
            shader.alpha = 0.5f

            if (!isCasual) {
                disableSessionController()
            } else {
                disableSessionControllerCasual()
            }
            startSession()
        }
    }

    override fun onBackPressed() {
        if (!isCasual) {
            Intent(Intent.ACTION_MAIN).also {
                it.addCategory(Intent.CATEGORY_HOME)
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                startActivity(it)
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        activeExercise = exerciseList[pos]

        when(activeExercise.type) {
            Exercise.ExerciseType.StepExercise -> {
                targetLabel.text = "You need to take at least\n${activeExercise.target} steps"
                progressValueLabel.text = "steps done"
                progressValueTextView.text = "0"
            }
            Exercise.ExerciseType.DurationExercise -> {
                val time = Time(activeExercise.target as Long)

                val timeString: String

                if (time.hours == 0L) {
                    timeString = if (time.seconds == 0L) {
                        "${time.minutes} minutes"
                    } else if (time.seconds > 1) {
                        "${time.minutes} minutes and ${time.seconds} seconds"
                    } else {
                        "${time.minutes} minutes and ${time.seconds} second"
                    }
                } else {
                    timeString = if (time.minutes == 0L) {
                        if (time.hours > 1) {
                            "${time.hours} hours"
                        } else {
                            "${time.hours} hour"
                        }
                    } else {
                        if (time.hours > 1) {
                            if (time.minutes > 1) {
                                "${time.hours} hours and ${time.minutes} minutes"
                            } else {
                                "${time.hours} hours and ${time.minutes} minute"
                            }
                        } else {
                            if (time.minutes > 1) {
                                "${time.hours} hour and ${time.minutes} minutes"
                            } else {
                                "${time.hours} hour and ${time.minutes} minute"
                            }
                        }
                    }
                }

                targetLabel.text = "You need to last for at least\n${timeString}"
                progressValueLabel.text = "time elapsed"
                progressValueTextView.text = "00:00"
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

                    if (value >= target.toInt()) { enableSessionController() }
                }
            }
            Exercise.ExerciseType.DurationExercise -> {
                tracker = TimeTracker()
                tracker.setOnValueChangedListener { value ->
                    value as Long
                    val target = activeExercise.target as Long
                    val time = Time(value)

                    if (time.hours == 0L) {
                        val minString = time.minutes.toString().padStart(2, '0')
                        val secString = time.seconds.toString().padStart(2, '0')
                        progressValueTextView.text = "${minString}:${secString}"
                    } else {
                        val hrsString = time.hours.toString().padStart(2, '0')
                        val minString = time.minutes.toString().padStart(2, '0')
                        progressValueTextView.text = "${hrsString}:${minString}"
                    }

                    if(value >= target) { enableSessionController() }
                }
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
        Intent(applicationContext, AppWatcherService::class.java).also { intent ->
            stopService(intent)
        }

        recordSession()
        finish()
    }

    private fun recordSession() {
        Session(
            tracker.startTime,
            tracker.endTime,
            tracker.data
        ).also { session ->
            SessionsAPI.addSession(activeExercise.id, session)

            when (activeExercise.type) {
                Exercise.ExerciseType.StepExercise -> {
                    // FIXME: this is a temporary fix to the duplicate data. It should instead be subtracted from _default
                    return

                    val suggestedStepCount = 17_500.0
                    val heartPoints = 100.0
                    val ratio = (tracker.data as Int) / suggestedStepCount

                    val additionalExp = ratio * heartPoints

                    UsersAPI.addExp(additionalExp)
                }
                Exercise.ExerciseType.DurationExercise -> {
                    val millisecondsPerMinute = 60_000.0
                    val suggestedMoveMinutes = 150.0
                    val heartPoints = 100.0
                    val ratio = (tracker.data as Long) / (suggestedMoveMinutes * millisecondsPerMinute)

                    val additionalExp = ratio * heartPoints

                    UsersAPI.addExp(additionalExp)
                }
                else -> { }
            }
        }
    }

    private fun enableSessionController() {
        // notify user that exercise has been completed
        statusText.text = "Yay!\nYou may now use the app!"
        statusText.alpha = 1f

        // set session controller to end session on click
        sessionControllerButton.setOnClickListener {
            shader.alpha = 1.0f
            endSession()
        }

        // re-enable session controller
        sessionControllerButton.alpha = 1.0f
        sessionControllerButton.isEnabled = true
    }

    private fun disableSessionController() {
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
    }

    private fun disableSessionControllerCasual() {
        // Do not disable session controller
        sessionControllerButton.text = "Stop Exercise"

        // mute less important data
        statusText.alpha = 0.3f
        targetLabel.alpha = 0.5f
        sessionControllerButton.setOnClickListener {
            shader.alpha = 1.0f
            endSession()
        }

        // hide exercise changer and disable changing exercises
        exerciseListSpinner.alpha = 0.0f
        exerciseListSpinner.isEnabled = false
        findViewById<TextView>(R.id.change_exercise_label).alpha = 0.0f
    }
}