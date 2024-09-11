package org.rsmod.api.type.refs.stat

import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.stat.StatTypeBuilder

public abstract class StatReferences : NameTypeReferences<StatType>(StatType::class.java) {
    override fun find(internal: String): StatType {
        val type = StatTypeBuilder(internal).build(id = -1)
        cache += type
        return type
    }
}
