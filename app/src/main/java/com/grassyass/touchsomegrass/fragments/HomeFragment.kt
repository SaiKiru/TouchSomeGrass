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
import com.grassyass.touchsomegrass.activities.LoginActivity
import com.grassyass.touchsomegrass.data.models.User
import com.grassyass.touchsomegrass.data.network.Authentication
import com.grassyass.touchsomegrass.data.network.api.UsersAPI
import com.grassyass.touchsomegrass.views.BarGraph

class HomeFragment : Fragment() {
    private lateinit var userImageView: ImageView
    private lateinit var userNameLabel: TextView
    private lateinit var userLevelLabel: TextView
    private lateinit var userTitleLabel: TextView
    private lateinit var stepGraph: BarGraph
    private lateinit var logOutButton: ImageButton

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
        stepGraph = view.findViewById<FrameLayout>(R.id.step_graph).findViewById(R.id.graph)

        logOutButton.setOnClickListener { onLogOutButtonPressed() }

        stepGraph.setData(
            arrayListOf(720, 2432, 640, 2201, 2670, 800, 343),
            arrayListOf("S", "M", "T", "W", "T", "F", "S")
        )

        UsersAPI.getUser().addOnSuccessListener {
            it.ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)!!

                    userNameLabel.text = user.name
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
}