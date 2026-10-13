package org.runerealm.api.type.builders.droptrig

import org.runerealm.api.type.builders.NameTypeBuilder
import org.runerealm.game.type.droptrig.DropTriggerType
import org.runerealm.game.type.droptrig.DropTriggerTypeBuilder

public abstract class DropTriggerBuilder :
    NameTypeBuilder<DropTriggerTypeBuilder, DropTriggerType>() {
    override fun build(internal: String, init: DropTriggerTypeBuilder.() -> Unit): DropTriggerType {
        val type = DropTriggerTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
