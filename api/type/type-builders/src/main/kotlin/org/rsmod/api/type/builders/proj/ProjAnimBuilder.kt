package org.rsmod.api.type.builders.proj

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.game.type.proj.ProjAnimTypeBuilder
import org.rsmod.game.type.proj.UnpackedProjAnimType

public abstract class ProjAnimBuilder :
    HashTypeBuilder<ProjAnimTypeBuilder, UnpackedProjAnimType>() {
    override fun build(internal: String, init: ProjAnimTypeBuilder.() -> Unit) {
        val type = ProjAnimTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
