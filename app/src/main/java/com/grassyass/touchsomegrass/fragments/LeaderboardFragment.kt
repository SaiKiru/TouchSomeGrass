package com.grassyass.touchsomegrass.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.activities.FriendSearchActivity
import com.grassyass.touchsomegrass.adapters.LeaderboardListAdapter
import com.grassyass.touchsomegrass.data.models.User
import com.grassyass.touchsomegrass.data.network.Database
import com.grassyass.touchsomegrass.data.network.api.LeaderboardAPI
import com.grassyass.touchsomegrass.data.network.api.UsersAPI

class LeaderboardFragment : Fragment() {
    private lateinit var leaderboardListRV: RecyclerView
    private lateinit var findFriendsButton: Button
    private lateinit var globalRanksButton: Button
    private lateinit var friendsRanksButton: Button

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
        findFriendsButton = view.findViewById(R.id.find_friends_button)
        globalRanksButton = view.findViewById(R.id.global_ranks_button)
        friendsRanksButton = view.findViewById(R.id.friends_ranks_button)

        findFriendsButton.setOnClickListener { onFindFriendsButtonPressed() }
        globalRanksButton.setOnClickListener { onGlobalRankingsButtonPressed() }
        friendsRanksButton.setOnClickListener { onFriendsRankingsButtonPressed() }

        leaderboardListRV.adapter = LeaderboardListAdapter(view.context, arrayListOf())

        onGlobalRankingsButtonPressed()
    }

    private fun onFindFriendsButtonPressed() {
        Intent(context, FriendSearchActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun onGlobalRankingsButtonPressed() {
        globalRanksButton.setTextColor(resources.getColor(R.color.white))
        globalRanksButton.setBackgroundColor(resources.getColor(R.color.primary_700))

        friendsRanksButton.setTextColor(resources.getColor(R.color.primary_700))
        friendsRanksButton.setBackgroundColor(resources.getColor(R.color.primary_100))

        getGlobalRankings()
    }

    private fun onFriendsRankingsButtonPressed() {
        friendsRanksButton.setTextColor(resources.getColor(R.color.white))
        friendsRanksButton.setBackgroundColor(resources.getColor(R.color.primary_700))

        globalRanksButton.setTextColor(resources.getColor(R.color.primary_700))
        globalRanksButton.setBackgroundColor(resources.getColor(R.color.primary_100))

        getFriendsRankings()
    }

    private fun getGlobalRankings() {
        LeaderboardAPI.getGlobalRankings().addOnSuccessListener {
            it.ref.orderByChild("exp").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users: ArrayList<User> = ArrayList()

                    snapshot.children.forEach { userSnapshot ->
                        val user = userSnapshot.getValue(User::class.java)!!
                        user.id = userSnapshot.key!!

                        users.add(user)
                    }

                    leaderboardListRV.adapter = LeaderboardListAdapter(requireContext(), users.reversed())
                }

                override fun onCancelled(error: DatabaseError) { }

            })
        }
    }

    private fun getFriendsRankings() {
        UsersAPI.getUser().addOnSuccessListener {
            it.ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)!!
                    val friends: ArrayList<String>

                    if (user.friends.isNullOrEmpty()) {
                        leaderboardListRV.adapter = LeaderboardListAdapter(requireContext(), arrayListOf())
                        return
                    } else {
                        friends = user.friends!!
                    }

                    val friendsData = arrayListOf<User>()

                    for (i in 0 until friends.size) {
                        val friendID = friends[i]

                        Database.readData("/users/$friendID").addOnSuccessListener { friendSnapshot ->
                            val friendData = friendSnapshot.getValue(User::class.java)!!
                            friendsData.add(friendData)

                            if (i == friends.size - 1) {
                                val sortedFriends = friendsData.sortedByDescending { friend -> friend.exp }
                                leaderboardListRV.adapter = LeaderboardListAdapter(requireContext(), sortedFriends)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) { }
            })
        }
    }
}