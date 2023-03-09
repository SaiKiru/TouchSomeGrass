package com.grassyass.touchsomegrass.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.models.User
import com.grassyass.touchsomegrass.data.network.api.UsersAPI

class UserListAdapter(
    private val context: Context,
    private val userList: List<User>
) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_user_list_item, parent, false)

        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]

        holder.userNameLabel.text = user.name
        holder.userIdLabel.text = "id: ${user.id.slice(0 until 6)}"

        holder.setOnClickListener {
            UsersAPI.addFriend(user.id)
            (context as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val userNameLabel: TextView = view.findViewById(R.id.user_name_label)
        val userIdLabel: TextView = view.findViewById(R.id.user_id_label)

        fun setOnClickListener(onClickListener: OnClickListener) {
            view.setOnClickListener(onClickListener)
        }
    }
}