package com.grassyass.touchsomegrass.utils

import java.util.*

abstract class Tracker() {
    private var _data: Any? = null
    var startTime: Long? = null
    var endTime: Long? = null
    var listener: OnValueChangedListener? = null

    lateinit var data: Any
        fun get(): Any? { return _data}
        fun set(value: Any) {_data = value}

    open fun start() {
        startTime = Date().time
    }

    open fun end() {
        endTime = Date().time
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