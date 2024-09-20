package org.rsmod.api.type.refs.timer

import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.game.type.timer.TimerType
import org.rsmod.game.type.timer.TimerTypeBuilder

public abstract class TimerReferences : NameTypeReferences<TimerType>(TimerType::class.java) {
    override fun find(internal: String): TimerType {
        val type = TimerTypeBuilder(internalName = internal).build(id = -1)
        cache += type
        return type
    }
}
