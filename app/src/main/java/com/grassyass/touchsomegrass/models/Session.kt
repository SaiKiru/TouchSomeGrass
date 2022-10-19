package com.grassyass.touchsomegrass.models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Session(
    val start: Long? = null,
    val end: Long? = null,
    val data: Any? = null,
) : Serializable