package org.runerealm.api.type.builders.npc

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.game.type.npc.NpcTypeBuilder
import org.runerealm.game.type.npc.UnpackedNpcType

public abstract class NpcBuilder : HashTypeBuilder<NpcTypeBuilder, UnpackedNpcType>() {
    override fun build(internal: String, init: NpcTypeBuilder.() -> Unit) {
        val type = NpcTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
