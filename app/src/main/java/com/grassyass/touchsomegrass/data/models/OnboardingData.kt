package com.grassyass.touchsomegrass.data.models

import android.graphics.drawable.Drawable

data class OnboardingData(
    val header: String,
    val description: String,
    val illustration: Drawable? = null
)