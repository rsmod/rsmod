package org.runerealm.api.type.builders.proj

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.game.type.proj.ProjAnimTypeBuilder
import org.runerealm.game.type.proj.UnpackedProjAnimType

public abstract class ProjAnimBuilder :
    HashTypeBuilder<ProjAnimTypeBuilder, UnpackedProjAnimType>() {
    override fun build(internal: String, init: ProjAnimTypeBuilder.() -> Unit) {
        val type = ProjAnimTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
