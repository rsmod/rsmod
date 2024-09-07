package org.rsmod.api.type.refs.mod

import org.rsmod.api.type.refs.NameTypeReferences
import org.rsmod.game.type.mod.ModLevel

public abstract class ModLevelReferences : NameTypeReferences<ModLevel>(ModLevel::class.java) {
    override fun find(internal: String): ModLevel {
        val type = ModLevel(internalId = -1, internalName = internal)
        cache += type
        return type
    }
}
