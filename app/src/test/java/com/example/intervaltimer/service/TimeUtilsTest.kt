package com.example.intervaltimer.service

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeUtilsTest {

    // --- formatTime ---

    @Test
    fun `formatTime zero millis`() {
        assertEquals("00:00", TimeUtils.formatTime(0))
    }

    @Test
    fun `formatTime one second`() {
        assertEquals("00:01", TimeUtils.formatTime(1000))
    }

    @Test
    fun `formatTime one minute`() {
        assertEquals("01:00", TimeUtils.formatTime(60_000))
    }

    @Test
    fun `formatTime one minute and one second`() {
        assertEquals("01:01", TimeUtils.formatTime(61_000))
    }

    @Test
    fun `formatTime fifty nine minutes fifty nine seconds`() {
        assertEquals("59:59", TimeUtils.formatTime(3_599_000))
    }

    @Test
    fun `formatTime sixty minutes`() {
        assertEquals("60:00", TimeUtils.formatTime(3_600_000))
    }

    @Test
    fun `formatTime truncates sub-second remainder`() {
        // 1999ms -> 1 total second -> "00:01"
        assertEquals("00:01", TimeUtils.formatTime(1999))
    }

    // --- displaySeconds ---

    @Test
    fun `displaySeconds zero`() {
        assertEquals(0L, TimeUtils.displaySeconds(0))
    }

    @Test
    fun `displaySeconds one ms rounds up`() {
        assertEquals(1L, TimeUtils.displaySeconds(1))
    }

    @Test
    fun `displaySeconds 999ms rounds up`() {
        assertEquals(1L, TimeUtils.displaySeconds(999))
    }

    @Test
    fun `displaySeconds exact 1000ms`() {
        assertEquals(1L, TimeUtils.displaySeconds(1000))
    }

    @Test
    fun `displaySeconds 1001ms rounds up`() {
        assertEquals(2L, TimeUtils.displaySeconds(1001))
    }

    @Test
    fun `displaySeconds 4999ms rounds up`() {
        assertEquals(5L, TimeUtils.displaySeconds(4999))
    }

    @Test
    fun `displaySeconds exact 5000ms`() {
        assertEquals(5L, TimeUtils.displaySeconds(5000))
    }

    @Test
    fun `displaySeconds 5001ms rounds up`() {
        assertEquals(6L, TimeUtils.displaySeconds(5001))
    }

    // --- toMillis ---

    @Test
    fun `toMillis zero`() {
        assertEquals(0L, TimeUtils.toMillis(0, 0))
    }

    @Test
    fun `toMillis one minute`() {
        assertEquals(60_000L, TimeUtils.toMillis(1, 0))
    }

    @Test
    fun `toMillis thirty seconds`() {
        assertEquals(30_000L, TimeUtils.toMillis(0, 30))
    }

    @Test
    fun `toMillis five minutes thirty seconds`() {
        assertEquals(330_000L, TimeUtils.toMillis(5, 30))
    }

    @Test
    fun `toMillis max picker values`() {
        assertEquals(3_599_000L, TimeUtils.toMillis(59, 59))
    }
}
