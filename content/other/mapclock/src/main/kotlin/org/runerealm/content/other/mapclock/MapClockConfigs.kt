package org.runerealm.content.other.mapclock

import org.runerealm.api.type.refs.timer.TimerReferences

typealias clock_timers = MapClockTimers

object MapClockTimers : TimerReferences() {
    val map_clock = find("map_clock")
}
