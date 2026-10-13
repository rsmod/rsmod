package org.runerealm.api.type.refs.timer

import org.runerealm.api.type.refs.NameTypeReferences
import org.runerealm.game.type.timer.TimerType
import org.runerealm.game.type.timer.TimerTypeBuilder

public abstract class TimerReferences : NameTypeReferences<TimerType>(TimerType::class.java) {
    override fun find(internal: String): TimerType {
        val type = TimerTypeBuilder(internalName = internal).build(id = -1)
        cache += type
        return type
    }
}
