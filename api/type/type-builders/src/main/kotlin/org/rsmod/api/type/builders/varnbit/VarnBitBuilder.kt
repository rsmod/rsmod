package org.rsmod.api.type.builders.varnbit

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.api.type.script.dsl.VarnBitPluginBuilder
import org.rsmod.game.type.varnbit.UnpackedVarnBitType

public abstract class VarnBitBuilder :
    HashTypeBuilder<VarnBitPluginBuilder, UnpackedVarnBitType>() {
    override fun build(internal: String, init: VarnBitPluginBuilder.() -> Unit) {
        val type = VarnBitPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
