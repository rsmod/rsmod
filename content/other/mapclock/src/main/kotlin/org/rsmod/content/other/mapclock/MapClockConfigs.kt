package org.rsmod.content.other.mapclock

import org.rsmod.api.type.refs.timer.TimerReferences

typealias clock_timers = MapClockTimers

object MapClockTimers : TimerReferences() {
    val map_clock = find("map_clock")
}
