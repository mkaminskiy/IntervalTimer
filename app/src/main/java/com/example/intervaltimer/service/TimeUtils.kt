package com.example.intervaltimer.service

import kotlin.math.ceil

internal object TimeUtils {
    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun displaySeconds(millis: Long): Long =
        ceil(millis / 1000.0).toLong()

    fun toMillis(minutes: Int, seconds: Int): Long =
        ((minutes * 60L) + seconds) * 1000L
}
