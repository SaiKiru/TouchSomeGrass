package com.grassyass.touchsomegrass.data.models

import com.grassyass.touchsomegrass.adapters.AppListAdapter

data class AppList(
    val title: String,
    val description: String,
    val appListAdapter: AppListAdapter
)
