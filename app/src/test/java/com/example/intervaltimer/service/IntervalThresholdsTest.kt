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

    @Test
    fun `build returns empty for negative totalSeconds`() {
        assertTrue(IntervalThresholds.build(totalSeconds = -5, intervals = 3).isEmpty())
    }

    @Test
    fun `build returns empty for negative intervals`() {
        assertTrue(IntervalThresholds.build(totalSeconds = 10, intervals = -2).isEmpty())
    }

    @Test
    fun `build returns empty when totalSeconds is 1 and intervals is 2`() {
        // 1 until 1 is empty range, so filter produces nothing
        val thresholds = IntervalThresholds.build(totalSeconds = 1, intervals = 2)

        assertTrue(thresholds.isEmpty())
    }

    @Test
    fun `build returns single threshold for minimal working case`() {
        val thresholds = IntervalThresholds.build(totalSeconds = 2, intervals = 2)

        assertEquals(listOf(1L), thresholds)
    }

    @Test
    fun `build handles even division`() {
        val thresholds = IntervalThresholds.build(totalSeconds = 12, intervals = 4)

        assertEquals(listOf(9L, 6L, 3L), thresholds)
    }

    @Test
    fun `build handles many intervals`() {
        val thresholds = IntervalThresholds.build(totalSeconds = 60, intervals = 10)

        assertEquals(listOf(54L, 48L, 42L, 36L, 30L, 24L, 18L, 12L, 6L), thresholds)
    }

    @Test
    fun `build handles totalSeconds equal to intervals`() {
        val thresholds = IntervalThresholds.build(totalSeconds = 5, intervals = 5)

        assertEquals(listOf(4L, 3L, 2L, 1L), thresholds)
    }

    @Test
    fun `build handles large values`() {
        val thresholds = IntervalThresholds.build(totalSeconds = 3600, intervals = 4)

        assertEquals(listOf(2700L, 1800L, 900L), thresholds)
    }
}
