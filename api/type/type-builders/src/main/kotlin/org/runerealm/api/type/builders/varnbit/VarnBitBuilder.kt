package org.runerealm.api.type.builders.varnbit

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.api.type.script.dsl.VarnBitPluginBuilder
import org.runerealm.game.type.varnbit.UnpackedVarnBitType

public abstract class VarnBitBuilder :
    HashTypeBuilder<VarnBitPluginBuilder, UnpackedVarnBitType>() {
    override fun build(internal: String, init: VarnBitPluginBuilder.() -> Unit) {
        val type = VarnBitPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
