package org.rsmod.api.type.editors.npc

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.NpcPluginBuilder
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.npc.UnpackedNpcType

public abstract class NpcEditor : TypeEditor<UnpackedNpcType>() {
    public fun edit(type: NpcType, init: NpcPluginBuilder.() -> Unit) {
        val type = NpcPluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
