package org.rsmod.api.type.refs.walktrig

import org.rsmod.api.type.refs.TypeReferences
import org.rsmod.game.type.walktrig.WalkTriggerType
import org.rsmod.game.type.walktrig.WalkTriggerTypeBuilder

public abstract class WalkTriggerReferences :
    TypeReferences<WalkTriggerType, Nothing>(WalkTriggerType::class.java) {
    public fun find(internal: String): WalkTriggerType {
        val type = WalkTriggerTypeBuilder(internal).build(-1)
        cache += type
        return type
    }
}
