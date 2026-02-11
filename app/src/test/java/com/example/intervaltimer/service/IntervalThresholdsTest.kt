package com.example.intervaltimer.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IntervalThresholdsTest {

    @Test
    fun `build returns midpoint for two intervals`() {
        val thresholds = IntervalThresholds.build(totalSeconds = 10, intervals = 2)

        assertEquals(listOf(5L), thresholds)
    }

    @Test
    fun `build keeps descending unique positive thresholds`() {
        val thresholds = IntervalThresholds.build(totalSeconds = 10, intervals = 3)

        assertEquals(listOf(7L, 4L), thresholds)
        assertTrue(thresholds.all { it > 0 })
        assertTrue(thresholds.zipWithNext().all { (left, right) -> left > right })
    }

    @Test
    fun `build handles intervals greater than total seconds`() {
        val thresholds = IntervalThresholds.build(totalSeconds = 3, intervals = 10)

        assertEquals(listOf(2L, 1L), thresholds)
    }

    @Test
    fun `build returns empty for invalid input`() {
        assertTrue(IntervalThresholds.build(totalSeconds = 0, intervals = 2).isEmpty())
        assertTrue(IntervalThresholds.build(totalSeconds = 10, intervals = 1).isEmpty())
        assertTrue(IntervalThresholds.build(totalSeconds = 10, intervals = 0).isEmpty())
    }
}
