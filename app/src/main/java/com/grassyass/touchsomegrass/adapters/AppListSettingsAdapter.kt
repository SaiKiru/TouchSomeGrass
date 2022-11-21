package com.grassyass.touchsomegrass.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.models.AppList

class AppListSettingsAdapter(
    private val context: Context,
    private val appList: List<AppList>
) : RecyclerView.Adapter<AppListSettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_app_list_recycler, parent, false)

        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appListData = appList[position]

        holder.listTitleLabel.text = appListData.title
        holder.listDescriptionLabel.text = appListData.description
        holder.appListRecycler.adapter = appListData.appListAdapter
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val listTitleLabel: TextView = view.findViewById(R.id.list_title_label)
        val listDescriptionLabel: TextView = view.findViewById(R.id.list_description_label)
        val appListRecycler: RecyclerView = view.findViewById(R.id.app_list_recycler)
    }
}