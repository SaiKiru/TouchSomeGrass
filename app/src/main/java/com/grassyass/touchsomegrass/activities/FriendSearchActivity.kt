package com.grassyass.touchsomegrass.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.adapters.UserListAdapter
import com.grassyass.touchsomegrass.data.models.User
import com.grassyass.touchsomegrass.data.network.api.UsersAPI

class FriendSearchActivity : AppCompatActivity() {
    private lateinit var searchField: EditText
    private lateinit var friendResultsRV: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_search)

        searchField = findViewById(R.id.search_field)
        friendResultsRV = findViewById(R.id.results_rv)

        searchField.setOnEditorActionListener { field, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFriend(field.text.toString().trim())
                return@setOnEditorActionListener true
            }

            false
        }

        friendResultsRV.adapter = UserListAdapter(this, arrayListOf())
    }

    private fun searchFriend(id: String = "") {
        if (id.isEmpty()) return

        UsersAPI.getUsers().addOnSuccessListener {
            it.ref
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val users: ArrayList<User> = ArrayList()

                        run iteration@{
                            snapshot.children.forEach { userSnapshot ->
                                val user = userSnapshot.getValue(User::class.java)!!
                                user.id = userSnapshot.key!!

                                if (id == user.id.slice(0 until 6)
                                    || (!user.name.isNullOrEmpty()
                                            && user.name!!.contains(id, true))
                                ) {

                                    users.add(user)

                                    if (users.size >= 10) return@iteration
                                }
                            }
                        }

                        friendResultsRV.adapter = UserListAdapter(this@FriendSearchActivity, users)
                    }

                    override fun onCancelled(error: DatabaseError) { }
                }
            )
        }
    }
}