package com.grassyass.touchsomegrass.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.activities.ExerciseSettingsActivity
import com.grassyass.touchsomegrass.data.models.Exercise

class ExerciseListAdapter(
    private val context: Context,
    private val exerciseList: List<Exercise>
) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_exercise_list_item, parent, false)

        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = exerciseList[position]

        holder.exerciseNameLabel.text = item.name
        holder.setOnClickListener {
            Intent(context, ExerciseSettingsActivity::class.java).also {
                it.putExtra(context.getString(R.string.extra_edit_exercise), item)
                context.startActivity(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val exerciseNameLabel: TextView = view.findViewById(R.id.exercise_name_label)

        fun setOnClickListener(onClickListener: OnClickListener) {
            view.setOnClickListener(onClickListener)
        }
    }
}