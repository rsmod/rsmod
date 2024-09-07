package org.rsmod.utils.time

import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.rsmod.utils.time.InlineLocalDateTime.Companion.YEAR_BIT_MASK
import org.rsmod.utils.time.InlineLocalDateTime.Companion.YEAR_OFFSET

class InlineLocalDateTimeTest {
    @Test
    fun `inline now`() {
        val now = LocalDateTime.now()
        val inline = InlineLocalDateTime(now)
        assertEquals(now.year, inline.year)
        assertEquals(now.monthValue, inline.month)
        assertEquals(now.dayOfMonth, inline.day)
        assertEquals(now.hour, inline.hour)
        assertEquals(now.minute, inline.minute)
        assertEquals(now.second, inline.second)
    }

    @Test
    fun `inline today`() {
        val base = LocalDateTime.MIN.withYear(YEAR_OFFSET)
        val today = base.withMonth(1).withDayOfMonth(1).withHour(0)
        val inline = InlineLocalDateTime(base).withMonth(1).withDay(1).withHour(0)
        assertEquals(today.year, inline.year)
        assertEquals(today.monthValue, inline.month)
        assertEquals(today.dayOfMonth, inline.day)
        assertEquals(today.hour, inline.hour)
        assertEquals(today.minute, inline.minute)
        assertEquals(today.second, inline.second)
    }

    @Test
    fun `inline yesterday`() {
        val base = LocalDateTime.MIN.withYear(YEAR_OFFSET)
        val yesterday = base.withMonth(2).withDayOfMonth(1).minusDays(1)
        val inline = InlineLocalDateTime(base).withMonth(2).withDay(1).minusDays(1)
        assertEquals(yesterday.year, inline.year)
        assertEquals(yesterday.monthValue, inline.month)
        assertEquals(yesterday.dayOfMonth, inline.day)
        assertEquals(yesterday.hour, inline.hour)
        assertEquals(yesterday.minute, inline.minute)
        assertEquals(yesterday.second, inline.second)
    }

    @Test
    fun `inline tomorrow`() {
        val base = LocalDateTime.MIN.withYear(YEAR_OFFSET)
        val tomorrow = base.withMonth(12).withDayOfMonth(31).plusDays(1)
        val inline = InlineLocalDateTime(base).withMonth(12).withDay(31).plusDays(1)
        assertEquals(tomorrow.year, inline.year)
        assertEquals(tomorrow.monthValue, inline.month)
        assertEquals(tomorrow.dayOfMonth, inline.day)
        assertEquals(tomorrow.hour, inline.hour)
        assertEquals(tomorrow.minute, inline.minute)
        assertEquals(tomorrow.second, inline.second)
    }

    @Test
    fun `inline next week`() {
        val base = LocalDateTime.MIN.withYear(YEAR_OFFSET)
        val nextWeek = base.withMonth(5).withDayOfMonth(1).plusDays(7)
        val inline = InlineLocalDateTime(base).withMonth(5).withDay(1).plusDays(7)
        assertEquals(nextWeek.year, inline.year)
        assertEquals(nextWeek.monthValue, inline.month)
        assertEquals(nextWeek.dayOfMonth, inline.day)
        assertEquals(nextWeek.hour, inline.hour)
        assertEquals(nextWeek.minute, inline.minute)
        assertEquals(nextWeek.second, inline.second)
    }

    @Test
    fun `inline next year`() {
        val base = LocalDateTime.MIN.withYear(YEAR_OFFSET)
        val nextYear = base.withMonth(1).withDayOfMonth(10).plusYears(1)
        val inline = InlineLocalDateTime(base).withMonth(1).withDay(10).plusYears(1)
        assertEquals(nextYear.year, inline.year)
        assertEquals(nextYear.monthValue, inline.month)
        assertEquals(nextYear.dayOfMonth, inline.day)
        assertEquals(nextYear.hour, inline.hour)
        assertEquals(nextYear.minute, inline.minute)
        assertEquals(nextYear.second, inline.second)
    }

    @Test
    fun `throw error on year out of bound`() {
        val maxYear = YEAR_OFFSET + YEAR_BIT_MASK
        val minYear = YEAR_OFFSET
        assertDoesNotThrow { InlineLocalDateTime(minYear, 0, 0, 0, 0, 0) }
        assertThrows<IllegalArgumentException> {
            InlineLocalDateTime(minYear - 1, 12, 31, 23, 59, 59)
        }
        assertDoesNotThrow { InlineLocalDateTime(maxYear, 12, 31, 23, 59, 59) }
        assertThrows<IllegalArgumentException> { InlineLocalDateTime(maxYear + 1, 0, 0, 0, 0, 0) }
    }

    @Test
    fun `ensure this year is not out of bound`() {
        val thisYear = LocalDateTime.now().year
        assertDoesNotThrow { InlineLocalDateTime(thisYear, 12, 31, 23, 59, 59) }
    }
}
