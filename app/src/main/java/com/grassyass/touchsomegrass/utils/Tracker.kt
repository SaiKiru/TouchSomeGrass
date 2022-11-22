package com.grassyass.touchsomegrass.utils

import java.util.*

abstract class Tracker() {
    var data: Any? = null
    var startTime: Long? = null
    var endTime: Long? = null
    var listener: OnValueChangedListener? = null

    open fun start() {
        startTime = Date().time
    }

    open fun end() {
        endTime = Date().time
    }

    open fun reset() {
        startTime = null
        endTime = null
        data = null
    }

    open fun setOnValueChangedListener(onValueChangedListener: OnValueChangedListener) {
        listener = onValueChangedListener
    }

    open fun setOnValueChangedListener(onValueChangedListener: (value: Any?) -> Unit) {
        listener = object : OnValueChangedListener {
            override fun onValueChanged(value: Any?) {
                onValueChangedListener(value)
            }
        }
    }

    interface OnValueChangedListener {
        fun onValueChanged(value: Any?)
    }
}