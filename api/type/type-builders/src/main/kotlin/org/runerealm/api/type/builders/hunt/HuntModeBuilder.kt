package org.runerealm.api.type.builders.hunt

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.api.type.script.dsl.HuntModePluginBuilder
import org.runerealm.game.type.hunt.UnpackedHuntModeType

public abstract class HuntModeBuilder :
    HashTypeBuilder<HuntModePluginBuilder, UnpackedHuntModeType>() {
    override fun build(internal: String, init: HuntModePluginBuilder.() -> Unit) {
        val type = HuntModePluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
