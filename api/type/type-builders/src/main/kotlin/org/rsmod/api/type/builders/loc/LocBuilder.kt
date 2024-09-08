package org.rsmod.api.type.builders.loc

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.api.type.script.dsl.LocPluginBuilder
import org.rsmod.game.type.loc.UnpackedLocType

public abstract class LocBuilder : HashTypeBuilder<LocPluginBuilder, UnpackedLocType>() {
    override fun build(internal: String, init: LocPluginBuilder.() -> Unit) {
        val type = LocPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
