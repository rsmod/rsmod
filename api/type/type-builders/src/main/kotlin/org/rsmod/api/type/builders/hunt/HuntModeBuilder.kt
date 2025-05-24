package org.rsmod.api.type.builders.hunt

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.api.type.script.dsl.HuntModePluginBuilder
import org.rsmod.game.type.hunt.UnpackedHuntModeType

public abstract class HuntModeBuilder :
    HashTypeBuilder<HuntModePluginBuilder, UnpackedHuntModeType>() {
    override fun build(internal: String, init: HuntModePluginBuilder.() -> Unit) {
        val type = HuntModePluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
