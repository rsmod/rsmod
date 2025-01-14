package org.rsmod.api.type.builders.droptrig

import org.rsmod.api.type.builders.NameTypeBuilder
import org.rsmod.game.type.droptrig.DropTriggerType
import org.rsmod.game.type.droptrig.DropTriggerTypeBuilder

public abstract class DropTriggerBuilder :
    NameTypeBuilder<DropTriggerTypeBuilder, DropTriggerType>() {
    override fun build(internal: String, init: DropTriggerTypeBuilder.() -> Unit): DropTriggerType {
        val type = DropTriggerTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
