package com.grassyass.touchsomegrass.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.recyclerview.widget.RecyclerView
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.models.AppData
import java.util.*
import kotlin.collections.ArrayList

class AppListAdapter(
    private val context: Context,
    private val appList: ArrayList<AppData>
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    private var _initialItemState: Boolean = false

    var listener: OnItemClickListener? = null
    var initialItemState: Boolean
        get() { return _initialItemState }
        set(isChecked) { _initialItemState = isChecked}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_app_list_item, parent, false)

        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = appList[position]

        holder.appSwitch.text = app.appName
        holder.appSwitch.setCompoundDrawables(app.appIcon, null, null, null)
        holder.appSwitch.isChecked = initialItemState
        holder.packageName = app.packageName

        holder.appSwitch.setOnClickListener {
            listener?.onItemClick(app, position)
        }
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    fun addApp(app: AppData) {
        appList.add(app)
        appList.sortBy { appItem -> appItem.appName.lowercase(Locale.ROOT) }
        notifyDataSetChanged()
    }

    fun removeAppAt(position: Int) {
        appList.removeAt(position)
        appList.sortBy { appItem -> appItem.appName.lowercase(Locale.ROOT) }
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        listener = onItemClickListener
    }

    fun setOnItemClickListener(onItemClickListener: (app: AppData, position: Int) -> Unit) {
        listener = object : OnItemClickListener {
            override fun onItemClick(app: AppData, position: Int) {
                onItemClickListener(app, position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(app: AppData, position: Int)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val appSwitch: Switch = view.findViewById(R.id.app_switch)
        var packageName: String = ""
    }
}