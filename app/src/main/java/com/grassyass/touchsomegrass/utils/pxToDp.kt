package com.grassyass.touchsomegrass.utils

import android.content.Context
import android.util.TypedValue

fun pxToDp(context: Context, px: Float) : Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        px,
        context.resources.displayMetrics
    )
}