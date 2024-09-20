package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.timer.TimerReferences
import org.rsmod.game.type.timer.TimerType

public typealias timers = BaseTimers

public object BaseTimers : TimerReferences() {
    public val toxins: TimerType = find("toxins")
}
