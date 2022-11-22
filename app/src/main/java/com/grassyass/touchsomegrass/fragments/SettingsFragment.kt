package com.grassyass.touchsomegrass.fragments

import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grassyass.touchsomegrass.BuildConfig
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.adapters.AppListAdapter
import com.grassyass.touchsomegrass.adapters.AppListSettingsAdapter
import com.grassyass.touchsomegrass.data.local.Whitelist
import com.grassyass.touchsomegrass.data.models.AppData
import com.grassyass.touchsomegrass.data.models.AppList
import com.grassyass.touchsomegrass.utils.dpToPx
import java.util.*
import kotlin.collections.ArrayList

class SettingsFragment : Fragment() {
    private lateinit var appListRecycler: RecyclerView
    private lateinit var localWhitelist: ArrayList<String>
    private var apps: List<ApplicationInfo> = emptyList()
    private var whitelist: ArrayList<AppData> = arrayListOf()
    private var blacklist: ArrayList<AppData> = arrayListOf()
    private val criticalSystemApps: List<String> = listOf(
        "com.android.contacts",
        "com.android.settings",
        "com.android.vending"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localWhitelist = Whitelist.getAppList()

        val packageManager = requireContext().packageManager
        apps = packageManager.getInstalledApplications(0)

        // get only launch-able apps
        apps = apps.filter { app ->
            return@filter packageManager.getLaunchIntentForPackage(app.packageName) != null
        }

        // offload data loading during app start for better user experience
        apps.forEach { app ->
            if (app.packageName == BuildConfig.APPLICATION_ID
                || criticalSystemApps.contains(app.packageName)
            ) {
                return@forEach
            }

            val packageName = app.packageName
            val appName = packageManager.getApplicationLabel(app).toString()
            val appIcon = packageManager.getApplicationIcon(app)

            val iconSize = dpToPx(requireContext(), 32F).toInt()
            appIcon.setBounds(0, 0, iconSize, iconSize)

            val appData = AppData(packageName, appName, appIcon)

            localWhitelist.forEach whitelist@{ whitelistedApp ->
                if (whitelistedApp == packageName) {
                    whitelist.add(appData)
                    return@forEach
                }
            }

            blacklist.add(appData)
        }

        whitelist.sortBy { app -> app.appName.lowercase(Locale.ROOT) }
        blacklist.sortBy { app -> app.appName.lowercase(Locale.ROOT) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appListRecycler = view.findViewById(R.id.app_list_recycler)

        val whitelistAdapter = AppListAdapter(requireContext(), whitelist)
        val blacklistAdapter = AppListAdapter(requireContext(), blacklist)

        whitelistAdapter.setOnItemClickListener { app, position ->
            whitelistAdapter.removeAppAt(position)
            blacklistAdapter.addApp(app)
            Whitelist.removeApp(app.packageName)
        }

        blacklistAdapter.setOnItemClickListener { app, position ->
            blacklistAdapter.removeAppAt(position)
            whitelistAdapter.addApp(app)
            Whitelist.addApp(app.packageName)
        }

        whitelistAdapter.initialItemState = true

        appListRecycler.adapter = AppListSettingsAdapter(view.context, listOf(
            AppList(
                "Whitelist",
                "We won't lock the following apps:",
                whitelistAdapter
            ),
            AppList(
                "Blacklist",
                "These apps will be locked when you need to exercise:",
                blacklistAdapter
            )
        ))
    }
}