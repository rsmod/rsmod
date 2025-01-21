package org.rsmod.api.type.builders.varp

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.api.type.script.dsl.VarpPluginBuilder
import org.rsmod.game.type.varp.UnpackedVarpType

public abstract class VarpBuilder : HashTypeBuilder<VarpPluginBuilder, UnpackedVarpType>() {
    override fun build(internal: String, init: VarpPluginBuilder.() -> Unit) {
        val type = VarpPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
