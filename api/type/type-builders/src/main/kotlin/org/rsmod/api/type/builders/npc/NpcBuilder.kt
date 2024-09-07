package org.rsmod.api.type.builders.npc

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.game.type.npc.NpcTypeBuilder
import org.rsmod.game.type.npc.UnpackedNpcType

public abstract class NpcBuilder : HashTypeBuilder<NpcTypeBuilder, UnpackedNpcType>() {
    override fun build(internal: String, init: NpcTypeBuilder.() -> Unit) {
        val type = NpcTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
