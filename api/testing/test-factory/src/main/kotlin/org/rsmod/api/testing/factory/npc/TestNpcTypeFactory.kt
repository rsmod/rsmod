package org.rsmod.api.testing.factory.npc

import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.type.npc.NpcTypeBuilder
import org.rsmod.game.type.npc.UnpackedNpcType

public class TestNpcTypeFactory {
    public fun create(id: Int = 0, init: NpcTypeBuilder.() -> Unit = {}): UnpackedNpcType {
        val builder =
            NpcTypeBuilder().apply {
                internal = "test_npc_type"
                desc = "$this"
                defaultMode = NpcMode.None
            }
        return builder.apply(init).build(id)
    }
}
