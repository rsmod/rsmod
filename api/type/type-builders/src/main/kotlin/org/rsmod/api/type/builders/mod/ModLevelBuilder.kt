package org.rsmod.api.type.builders.mod

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.api.type.script.dsl.ModLevelPluginBuilder
import org.rsmod.game.type.mod.UnpackedModLevelType

public abstract class ModLevelBuilder :
    HashTypeBuilder<ModLevelPluginBuilder, UnpackedModLevelType>() {
    override fun build(internal: String, init: ModLevelPluginBuilder.() -> Unit) {
        val type = ModLevelPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
