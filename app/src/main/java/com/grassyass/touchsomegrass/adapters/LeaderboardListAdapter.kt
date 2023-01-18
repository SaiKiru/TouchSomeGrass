package com.grassyass.touchsomegrass.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.models.User

class LeaderboardListAdapter(
    private val context: Context,
    private val leaderboardList: List<User>
) : RecyclerView.Adapter<LeaderboardListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_leaderboard_list_item, parent, false)

        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = leaderboardList[position]

        holder.positionTV.text = (position + 1).toString()

        if (user.name.isNullOrEmpty()) {
            holder.nameTV.text = "Anonymous"
        } else {
            holder.nameTV.text = user.name!!.split(" ")[0]
        }

        holder.expTV.text = "${user.exp?.toInt()} XP"
    }

    override fun getItemCount(): Int {
        return leaderboardList.size
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val positionTV: TextView = view.findViewById(R.id.position_tv)
        val nameTV: TextView = view.findViewById(R.id.name_tv)
        val expTV: TextView = view.findViewById(R.id.exp_tv)

        fun setOnClickListener(onClickListener: OnClickListener) {
            view.setOnClickListener(onClickListener)
        }
    }
}