package org.rsmod.api.type.builders.walktrig

import org.rsmod.api.type.builders.NameTypeBuilder
import org.rsmod.game.type.walktrig.WalkTriggerType
import org.rsmod.game.type.walktrig.WalkTriggerTypeBuilder

public abstract class WalkTriggerBuilder :
    NameTypeBuilder<WalkTriggerTypeBuilder, WalkTriggerType>() {
    override fun build(internal: String, init: WalkTriggerTypeBuilder.() -> Unit): WalkTriggerType {
        val type = WalkTriggerTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
