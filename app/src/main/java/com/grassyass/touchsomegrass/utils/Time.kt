package com.grassyass.touchsomegrass.utils

class Time(ms: Long) {
    private val msPerSec = 1000L
    private val secPerMin = 60L
    private val minPerHr = 60

    val hours = ms / msPerSec / secPerMin / minPerHr
    val minutes = (ms / msPerSec / secPerMin) % 60
    val seconds = (ms / msPerSec) % 60
}