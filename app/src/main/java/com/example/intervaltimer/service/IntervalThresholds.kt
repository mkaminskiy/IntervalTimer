package com.example.intervaltimer.service

import kotlin.math.ceil

internal object IntervalThresholds {
    fun build(totalSeconds: Long, intervals: Int): List<Long> {
        if (totalSeconds <= 0 || intervals <= 1) return emptyList()

        val intervalDuration = totalSeconds.toDouble() / intervals.toDouble()

        return (1 until intervals)
            .map { index ->
                ceil(totalSeconds - (index * intervalDuration)).toLong()
            }
            .filter { threshold ->
                threshold in 1 until totalSeconds
            }
            .distinct()
            .sortedDescending()
    }
}
