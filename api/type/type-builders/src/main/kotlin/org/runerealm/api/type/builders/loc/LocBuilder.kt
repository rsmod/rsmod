package org.runerealm.api.type.builders.loc

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.api.type.script.dsl.LocPluginBuilder
import org.runerealm.game.type.loc.UnpackedLocType

public abstract class LocBuilder : HashTypeBuilder<LocPluginBuilder, UnpackedLocType>() {
    override fun build(internal: String, init: LocPluginBuilder.() -> Unit) {
        val type = LocPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
