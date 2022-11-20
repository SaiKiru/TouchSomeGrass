package com.grassyass.touchsomegrass.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.activities.NewExerciseActivity
import com.grassyass.touchsomegrass.adapters.ExerciseListAdapter
import com.grassyass.touchsomegrass.data.models.Exercise
import com.grassyass.touchsomegrass.data.network.api.ExercisesAPI


class ExercisesFragment : Fragment() {
    private lateinit var exerciseList: RecyclerView
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercises, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab = view.findViewById(R.id.fab)
        exerciseList = view.findViewById(R.id.exercise_list)
        exerciseList.setHasFixedSize(true)

        var exerciseListData: ArrayList<Exercise> = ArrayList()

        ExercisesAPI.getExercises().addOnSuccessListener {
            it.ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newExercises: ArrayList<Exercise> = ArrayList()

                    snapshot.children.forEach { exerciseSnapshot ->
                        val exercise = exerciseSnapshot.getValue(Exercise::class.java)!!
                        exercise.id = exerciseSnapshot.key!!

                        newExercises.add(exercise)
                    }

                    exerciseListData = newExercises
                    exerciseList.adapter = ExerciseListAdapter(view.context, exerciseListData)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(view.context,"Could not load exercises", Toast.LENGTH_LONG)
                        .show()
                }
            })
        }

        val createExercise = registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val exercise = it.data?.getSerializableExtra(getString(R.string.extra_new_exercise))

                ExercisesAPI.addExercise(exercise!! as Exercise)
            }
        }

        exerciseList.adapter = ExerciseListAdapter(view.context, exerciseListData)

        fab.setOnClickListener {
            val intent = Intent(view.context, NewExerciseActivity::class.java)

            createExercise.launch(intent)
        }
    }
}