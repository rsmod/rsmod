package org.rsmod.api.type.builders.walktrig

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.game.type.walktrig.WalkTriggerType
import org.rsmod.game.type.walktrig.WalkTriggerTypeBuilder

public abstract class WalkTriggerBuilder :
    HashTypeBuilder<WalkTriggerTypeBuilder, WalkTriggerType>() {
    override fun build(internal: String, init: WalkTriggerTypeBuilder.() -> Unit) {
        val type = WalkTriggerTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
