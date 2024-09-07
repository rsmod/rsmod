package org.rsmod.api.type.builders.mod

import org.rsmod.api.type.builders.NameTypeBuilder
import org.rsmod.api.type.script.dsl.ModGroupPluginBuilder
import org.rsmod.game.type.mod.ModGroup

public abstract class ModGroupBuilder : NameTypeBuilder<ModGroupPluginBuilder, ModGroup>() {
    override fun build(internal: String, init: ModGroupPluginBuilder.() -> Unit): ModGroup {
        val type = ModGroupPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
