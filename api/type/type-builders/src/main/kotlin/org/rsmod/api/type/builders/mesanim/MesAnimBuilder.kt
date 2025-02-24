package org.rsmod.api.type.builders.mesanim

import org.rsmod.api.type.builders.NameTypeBuilder
import org.rsmod.api.type.script.dsl.MesAnimPluginBuilder
import org.rsmod.game.type.mesanim.UnpackedMesAnimType

public abstract class MesAnimBuilder :
    NameTypeBuilder<MesAnimPluginBuilder, UnpackedMesAnimType>() {
    override fun build(
        internal: String,
        init: MesAnimPluginBuilder.() -> Unit,
    ): UnpackedMesAnimType {
        val type = MesAnimPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
        return type
    }
}
