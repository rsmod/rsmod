package org.runerealm.api.type.builders.inv

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.api.type.script.dsl.InvPluginBuilder
import org.runerealm.game.type.inv.UnpackedInvType

public abstract class InvBuilder : HashTypeBuilder<InvPluginBuilder, UnpackedInvType>() {
    override fun build(internal: String, init: InvPluginBuilder.() -> Unit) {
        val type = InvPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
