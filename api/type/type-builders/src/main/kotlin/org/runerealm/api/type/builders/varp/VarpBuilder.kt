package org.runerealm.api.type.builders.varp

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.api.type.script.dsl.VarpPluginBuilder
import org.runerealm.game.type.varp.UnpackedVarpType

public abstract class VarpBuilder : HashTypeBuilder<VarpPluginBuilder, UnpackedVarpType>() {
    override fun build(internal: String, init: VarpPluginBuilder.() -> Unit) {
        val type = VarpPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
