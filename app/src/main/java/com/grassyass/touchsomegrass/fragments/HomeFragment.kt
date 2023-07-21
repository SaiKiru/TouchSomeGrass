package com.grassyass.touchsomegrass.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.activities.ExpHelpActivity
import com.grassyass.touchsomegrass.activities.LoginActivity
import com.grassyass.touchsomegrass.data.models.Session
import com.grassyass.touchsomegrass.data.models.User
import com.grassyass.touchsomegrass.data.network.Authentication
import com.grassyass.touchsomegrass.data.network.api.SessionsAPI
import com.grassyass.touchsomegrass.data.network.api.UsersAPI
import com.grassyass.touchsomegrass.fragments.dialogs.EditUsernameInputDialog
import com.grassyass.touchsomegrass.fragments.dialogs.EditUsernameInputDialog.EditUsernameInputDialogListener
import com.grassyass.touchsomegrass.fragments.stats.BarGraph.BarGraphFragment
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var userImageView: ImageView
    private lateinit var userNameLabel: TextView
    private lateinit var userLevelLabel: TextView
    private lateinit var userTitleLabel: TextView
    private lateinit var stepGraph: BarGraphFragment
    private lateinit var logOutButton: ImageButton
    private lateinit var editButton: ImageButton
    private lateinit var weeklyStatsButton: Button
    private lateinit var monthlyStatsButton: Button
    private lateinit var timeSpanLabel: TextView
    private lateinit var previousTimeSpanButton: ImageButton
    private lateinit var nextTimeSpanButton: ImageButton
    private lateinit var user: User
    private lateinit var expHelpButton: ImageButton
    private lateinit var stepGraphContainer: FrameLayout

    private var overallStatsTimeRange: String = "WEEK"
    private var overallStatsTimePointer: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userImageView = view.findViewById(R.id.userImageView)
        userNameLabel = view.findViewById(R.id.userNameLabel)
        userLevelLabel = view.findViewById(R.id.userLevelLabel)
        userTitleLabel = view.findViewById(R.id.userTitleLabel)
        logOutButton = view.findViewById(R.id.log_out_button)
        editButton = view.findViewById(R.id.edit_button)
        weeklyStatsButton = view.findViewById(R.id.weeklyStatsButton)
        monthlyStatsButton = view.findViewById(R.id.monthlyStatsButton)
        timeSpanLabel = view.findViewById(R.id.timeSpanLabel)
        previousTimeSpanButton = view.findViewById(R.id.previousTimeSpanButton)
        nextTimeSpanButton = view.findViewById(R.id.nextTimeSpanButton)
        stepGraphContainer = view.findViewById(R.id.step_graph)
        stepGraph = BarGraphFragment()
        expHelpButton = view.findViewById(R.id.exp_help_btn)

        weeklyStatsButton.setOnClickListener { onWeeklyStatsButtonPressed() }
        monthlyStatsButton.setOnClickListener { onMonthlyStatsButtonPressed() }
        previousTimeSpanButton.setOnClickListener { onPreviousTimeSpanButtonPressed() }
        nextTimeSpanButton.setOnClickListener { onNextTimeSpanButtonPressed() }
        logOutButton.setOnClickListener { onLogOutButtonPressed() }
        editButton.setOnClickListener { onEditButtonPressed() }

        childFragmentManager.beginTransaction().apply {
            replace(stepGraphContainer.id, stepGraph)
            commitAllowingStateLoss()
        }

        expHelpButton.setOnClickListener {
            Intent(context, ExpHelpActivity::class.java).also {
                startActivity(it)
            }
        }

        updateTimeLabel()
        populateGraph()

        UsersAPI.getUser().addOnSuccessListener {
            it.ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)!!

                    userNameLabel.text = user.name
                    userLevelLabel.text = "Level ${user.getLvl()}"
                }

                override fun onCancelled(error: DatabaseError) {
                    // empty code
                }
            })
        }
    }

    private fun onLogOutButtonPressed() {
        Authentication.signOut()
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun onEditButtonPressed() {
        val dialog = EditUsernameInputDialog()

        dialog.setOnDialogButtonClickListener(object: EditUsernameInputDialogListener {
            override fun onDialogPositiveClick(value: String) {
                if (value.isBlank()) { return }

                user.name = value

                UsersAPI.updateUser(user)
            }

            override fun onDialogNegativeClick() { }
        })

        dialog.show(childFragmentManager, "edit_username_dialog")
    }

    private fun onWeeklyStatsButtonPressed() {
        weeklyStatsButton.setTextColor(resources.getColor(R.color.white))
        weeklyStatsButton.setBackgroundColor(resources.getColor(R.color.primary_400))

        monthlyStatsButton.setTextColor(resources.getColor(R.color.primary_400))
        monthlyStatsButton.setBackgroundColor(resources.getColor(R.color.white))

        overallStatsTimeRange = "WEEK"
        overallStatsTimePointer = 0
        updateTimeLabel(overallStatsTimeRange, overallStatsTimePointer)
        populateGraph(overallStatsTimeRange, overallStatsTimePointer)
    }

    private fun onMonthlyStatsButtonPressed() {
        monthlyStatsButton.setTextColor(resources.getColor(R.color.white))
        monthlyStatsButton.setBackgroundColor(resources.getColor(R.color.primary_400))

        weeklyStatsButton.setTextColor(resources.getColor(R.color.primary_400))
        weeklyStatsButton.setBackgroundColor(resources.getColor(R.color.white))

        overallStatsTimeRange = "MONTH"
        overallStatsTimePointer = 0
        updateTimeLabel(overallStatsTimeRange, overallStatsTimePointer)
        populateGraph(overallStatsTimeRange, overallStatsTimePointer)
    }

    private fun onNextTimeSpanButtonPressed() {
        overallStatsTimePointer++
        updateTimeLabel(overallStatsTimeRange, overallStatsTimePointer)
        populateGraph(overallStatsTimeRange, overallStatsTimePointer)
    }

    private fun onPreviousTimeSpanButtonPressed() {
        overallStatsTimePointer--
        updateTimeLabel(overallStatsTimeRange, overallStatsTimePointer)
        populateGraph(overallStatsTimeRange, overallStatsTimePointer)
    }

    private fun updateTimeLabel(span: String = "WEEK", pointer: Int = 0) {
        val cal = Calendar.getInstance()

        if (span == "WEEK") {
            cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR) + pointer)

            cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
            val fromMonth = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
            val fromDay = cal.get(Calendar.DAY_OF_MONTH)
            val fromYear = cal.get(Calendar.YEAR)

            cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK))
            val toMonth = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
            val toDay = cal.get(Calendar.DAY_OF_MONTH)
            val toYear = cal.get(Calendar.YEAR)

            if (fromYear != toYear) {
                timeSpanLabel.text = "$fromMonth $fromDay $fromYear - $toMonth $toDay $toYear"
            } else if (fromMonth != toMonth) {
                timeSpanLabel.text = "$fromMonth $fromDay - $toMonth $toDay $toYear"
            } else {
                timeSpanLabel.text = "$fromMonth $fromDay - $toDay $toYear"
            }
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + (pointer * 4))

            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 4 * 4)
            val fromMonth = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)

            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 3)
            val toMonth = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)

            val year = cal.get(Calendar.YEAR)

            timeSpanLabel.text = "$fromMonth $year - $toMonth $year"
        }
    }

    private fun populateGraph(span: String = "WEEK", pointer: Int = 0) {
        val cal = Calendar.getInstance()
        val startTime: Double
        val endTime: Double

        if (span === "WEEK") {
            cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR) + pointer)

            cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
            startTime = cal.timeInMillis.toDouble()

            cal.set(Calendar.DAY_OF_WEEK, 7)
            endTime = cal.timeInMillis.toDouble()
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + (pointer * 4))

            // This looks stupid but it is not
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 4 * 4)
            startTime = cal.timeInMillis.toDouble()

            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 3)
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            endTime = cal.timeInMillis.toDouble()
        }

        SessionsAPI.getSessions("_default").addOnSuccessListener {
            it.ref
                .orderByChild("end")
                .startAt(startTime)
                .endAt(endTime)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val aggregate: ArrayList<Number> =
                            if (span == "WEEK") {
                                arrayListOf(0, 0, 0, 0, 0, 0, 0)
                            } else {
                                arrayListOf(0, 0, 0, 0)
                            }

                        snapshot.children.forEach { sessionSnapshot ->
                            val session = sessionSnapshot.getValue(Session::class.java)!!

                            cal.timeInMillis = session.end as Long

                            val idx: Int =
                                if (span == "WEEK") {
                                    cal.get(Calendar.DAY_OF_WEEK) - 1
                                } else {
                                    cal.get(Calendar.MONTH) % 4
                                }

                            val data =
                                if (session.data === null) 0L
                                else session.data as Long

                            aggregate[idx] = (aggregate[idx] as Int) + data.toInt()
                        }

                        if (span == "WEEK") {
                            stepGraph.setData(aggregate, arrayListOf("S", "M", "T", "W", "T", "F", "S"))
                        } else {
                            val cal2 = Calendar.getInstance()

                            cal2.set(Calendar.MONTH, cal2.get(Calendar.MONTH) + (pointer * 4))

                            cal2.set(Calendar.MONTH, cal2.get(Calendar.MONTH) / 4 * 4)
                            val monthNames: ArrayList<String> = arrayListOf("", "", "", "")

                            for (i in 0 until 4) {
                                val monthName = cal2.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
                                cal2.set(Calendar.MONTH, cal2.get(Calendar.MONTH) + 1)

                                monthNames[i] = monthName!!
                            }

                            stepGraph.setData(aggregate, monthNames)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) { }
            })
        }
    }
}