package com.grassyass.touchsomegrass.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.adapters.LeaderboardListAdapter
import com.grassyass.touchsomegrass.data.models.User
import com.grassyass.touchsomegrass.data.network.api.LeaderboardAPI

class LeaderboardFragment : Fragment() {
    private lateinit var leaderboardListRV: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        leaderboardListRV = view.findViewById(R.id.leaderboard_list_rv)

        var leaderboardListData: ArrayList<User> = ArrayList()

        LeaderboardAPI.getGlobalRankings().addOnSuccessListener {
            it.ref.orderByChild("exp").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users: ArrayList<User> = ArrayList()

                    snapshot.children.forEach { userSnapshot ->
                        val user = userSnapshot.getValue(User::class.java)!!
                        user.id = userSnapshot.key!!

                        users.add(user)
                    }

                    leaderboardListData = users
                    leaderboardListRV.adapter = LeaderboardListAdapter(view.context, leaderboardListData.reversed())
                }

                override fun onCancelled(error: DatabaseError) { }

            })
        }

        leaderboardListRV.adapter = LeaderboardListAdapter(view.context, leaderboardListData)
    }
}