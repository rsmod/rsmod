package org.rsmod.api.testing.factory.timer

import org.rsmod.game.type.timer.TimerType
import org.rsmod.game.type.timer.TimerTypeBuilder

public class TestTimerTypeFactory {
    public fun create(id: Int = 0, init: TimerTypeBuilder.() -> Unit = {}): TimerType {
        val builder = TimerTypeBuilder().apply { internalName = "test_timer_type" }
        return builder.apply(init).build(id)
    }
}
