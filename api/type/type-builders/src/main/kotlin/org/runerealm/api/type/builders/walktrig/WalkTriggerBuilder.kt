package org.runerealm.api.type.builders.walktrig

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.game.type.walktrig.WalkTriggerType
import org.runerealm.game.type.walktrig.WalkTriggerTypeBuilder

public abstract class WalkTriggerBuilder :
    HashTypeBuilder<WalkTriggerTypeBuilder, WalkTriggerType>() {
    override fun build(internal: String, init: WalkTriggerTypeBuilder.() -> Unit) {
        val type = WalkTriggerTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
