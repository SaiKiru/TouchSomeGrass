package com.grassyass.touchsomegrass.utils

import android.content.Context
import android.util.TypedValue

fun dpToPx(context: Context, px: Float) : Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        px,
        context.resources.displayMetrics
    )
}

fun dpToPx(context: Context, px: Int) : Float {
    return dpToPx(context, px.toFloat())
}