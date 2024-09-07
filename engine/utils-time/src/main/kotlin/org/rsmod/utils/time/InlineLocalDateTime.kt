package org.rsmod.utils.time

import java.time.LocalDateTime

@JvmInline
public value class InlineLocalDateTime(public val packed: Int) {
    public val year: Int
        get() =
            (if (packed == 0) 0 else YEAR_OFFSET) + ((packed shr YEAR_BIT_OFFSET) and YEAR_BIT_MASK)

    public val month: Int
        get() = (packed shr MONTH_BIT_OFFSET) and MONTH_BIT_MASK

    public val day: Int
        get() = (packed shr DAY_BIT_OFFSET) and DAY_BIT_MASK

    public val hour: Int
        get() = (packed shr HOUR_BIT_OFFSET) and HOUR_BIT_MASK

    public val minute: Int
        get() = (packed shr MINUTE_BIT_OFFSET) and MINUTE_BIT_MASK

    public val second: Int
        get() = (packed shr SECOND_BIT_OFFSET) and SECOND_BIT_MASK

    public constructor(time: LocalDateTime) : this(pack(time))

    public constructor(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int,
    ) : this(pack(year, month, day, hour, minute, second))

    public fun isNotToday(): Boolean = !isToday()

    public fun isToday(): Boolean = isSameDay(now())

    public fun isSameDay(other: InlineLocalDateTime): Boolean =
        other.day == day && other.month == month && other.year == year

    public fun withYear(year: Int): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().withYear(year))
    }

    public fun withMonth(month: Int): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().withMonth(month))
    }

    public fun withDay(day: Int): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().withDayOfMonth(day))
    }

    public fun withHour(hour: Int): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().withHour(hour))
    }

    public fun withMinute(minute: Int): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().withMinute(minute))
    }

    public fun withSecond(second: Int): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().withSecond(second))
    }

    public fun minusDays(days: Long): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().minusDays(days))
    }

    public fun plusDays(days: Long): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().plusDays(days))
    }

    public fun plusYears(years: Long): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().plusYears(years))
    }

    public fun minusHours(hours: Long): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().minusHours(hours))
    }

    public fun plusHours(hours: Long): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().plusHours(hours))
    }

    public fun minusMinutes(minutes: Long): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().minusMinutes(minutes))
    }

    public fun plusMinutes(minutes: Long): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().plusMinutes(minutes))
    }

    public fun minusSeconds(seconds: Long): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().minusSeconds(seconds))
    }

    public fun plusSeconds(seconds: Long): InlineLocalDateTime {
        return InlineLocalDateTime(toLocateDateTime().plusSeconds(seconds))
    }

    public fun toLocateDateTime(): LocalDateTime {
        return LocalDateTime.now()
            .withYear(year)
            .withMonth(month)
            .withDayOfMonth(day)
            .withHour(hour)
            .withMinute(minute)
            .withSecond(second)
    }

    override fun toString(): String {
        return "InlineLocalDateTime(" +
            "year=$year," +
            "month=$month," +
            "day=$day," +
            "hour=$hour," +
            "minute=$minute," +
            "second=$second" +
            ")"
    }

    public companion object {
        public val NULL: InlineLocalDateTime = InlineLocalDateTime(0)
        public val MAX: InlineLocalDateTime = max()
        public val MIN: InlineLocalDateTime = min()

        /**
         * We offset the year assuming that we will not need to go BACK in time too far in order to
         * fit all values into a single integer.
         *
         * The maximum year this date format supports is equal to
         *
         * `[YEAR_OFFSET] + (1 shl [YEAR_BIT_COUNT]) - 1`
         *
         * If anyone beyond that year is reading this, I'm sorry please don't come back in time to
         * get me. Just increase the offset!
         */
        public const val YEAR_OFFSET: Int = 2020

        public const val YEAR_BIT_COUNT: Int = 5
        public const val MONTH_BIT_COUNT: Int = 4
        public const val DAY_BIT_COUNT: Int = 5
        public const val HOUR_BIT_COUNT: Int = 5
        public const val MINUTE_BIT_COUNT: Int = 6
        public const val SECOND_BIT_COUNT: Int = 6

        public const val YEAR_BIT_OFFSET: Int = 0
        public const val MONTH_BIT_OFFSET: Int = YEAR_BIT_OFFSET + YEAR_BIT_COUNT
        public const val DAY_BIT_OFFSET: Int = MONTH_BIT_OFFSET + MONTH_BIT_COUNT
        public const val HOUR_BIT_OFFSET: Int = DAY_BIT_OFFSET + DAY_BIT_COUNT
        public const val MINUTE_BIT_OFFSET: Int = HOUR_BIT_OFFSET + HOUR_BIT_COUNT
        public const val SECOND_BIT_OFFSET: Int = MINUTE_BIT_OFFSET + MINUTE_BIT_COUNT

        public const val YEAR_BIT_MASK: Int = (1 shl YEAR_BIT_COUNT) - 1
        public const val MONTH_BIT_MASK: Int = (1 shl MONTH_BIT_COUNT) - 1
        public const val DAY_BIT_MASK: Int = (1 shl DAY_BIT_COUNT) - 1
        public const val HOUR_BIT_MASK: Int = (1 shl HOUR_BIT_COUNT) - 1
        public const val MINUTE_BIT_MASK: Int = (1 shl MINUTE_BIT_COUNT) - 1
        public const val SECOND_BIT_MASK: Int = (1 shl SECOND_BIT_COUNT) - 1

        public fun now(): InlineLocalDateTime = InlineLocalDateTime(LocalDateTime.now())

        private fun min(): InlineLocalDateTime =
            InlineLocalDateTime(
                year = YEAR_OFFSET,
                month = 1,
                day = 1,
                hour = 0,
                minute = 0,
                second = 0,
            )

        private fun max(): InlineLocalDateTime =
            InlineLocalDateTime(
                year = YEAR_OFFSET + YEAR_BIT_MASK,
                month = 12,
                day = 31,
                hour = 23,
                minute = 59,
                second = 59,
            )

        private fun pack(time: LocalDateTime): Int {
            return pack(
                time.year,
                time.monthValue,
                time.dayOfMonth,
                time.hour,
                time.minute,
                time.second,
            )
        }

        private fun pack(
            year: Int,
            month: Int,
            day: Int,
            hour: Int,
            minute: Int,
            second: Int,
        ): Int {
            val offsetYear = year - YEAR_OFFSET
            require(offsetYear in 0..YEAR_BIT_MASK) {
                val maxYear = YEAR_OFFSET + YEAR_BIT_MASK
                "`year` value must be within range [$YEAR_OFFSET..$maxYear]."
            }
            require(month in 0..MONTH_BIT_MASK) {
                "`month` value must be within range [0-$MONTH_BIT_MASK]."
            }
            require(day in 0..DAY_BIT_MASK) {
                "`day` value must be within range [0-$DAY_BIT_MASK]."
            }
            require(hour in 0..HOUR_BIT_MASK) {
                "`hour` value must be within range [0-$HOUR_BIT_MASK]."
            }
            require(minute in 0..MINUTE_BIT_MASK) {
                "`month` value must be within range [0-$MINUTE_BIT_MASK]."
            }
            require(second in 0..SECOND_BIT_MASK) {
                "`second` value must be within range [0-$SECOND_BIT_MASK]."
            }
            val packed =
                ((offsetYear and YEAR_BIT_MASK) shl YEAR_BIT_OFFSET) or
                    ((month and MONTH_BIT_MASK) shl MONTH_BIT_OFFSET) or
                    ((day and DAY_BIT_MASK) shl DAY_BIT_OFFSET) or
                    ((hour and HOUR_BIT_MASK) shl HOUR_BIT_OFFSET) or
                    ((minute and MINUTE_BIT_MASK) shl MINUTE_BIT_OFFSET) or
                    ((second and SECOND_BIT_MASK) shl SECOND_BIT_OFFSET)
            return packed
        }
    }
}
