package com.grassyass.touchsomegrass.adapters

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.recyclerview.widget.RecyclerView
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.utils.pxToDp

class AppListAdapter(
    private val context: Context,
    private val appList: List<ApplicationInfo>
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_app_list_item, parent, false)

        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = appList[position]

        val packageManager = context.packageManager

        val appName = packageManager.getApplicationLabel(app)
        val appIcon = packageManager.getApplicationIcon(app)

        val iconSize = pxToDp(context, 32F)
        appIcon.setBounds(0, 0, iconSize, iconSize)

        holder.appSwitch.text = appName
        holder.appSwitch.setCompoundDrawables(appIcon, null, null, null)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val appSwitch: Switch = view.findViewById(R.id.app_switch)
    }
}