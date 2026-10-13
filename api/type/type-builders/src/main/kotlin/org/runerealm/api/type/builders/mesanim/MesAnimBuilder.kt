package org.runerealm.api.type.builders.mesanim

import org.runerealm.api.type.builders.NameTypeBuilder
import org.runerealm.api.type.script.dsl.MesAnimPluginBuilder
import org.runerealm.game.type.mesanim.UnpackedMesAnimType

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
