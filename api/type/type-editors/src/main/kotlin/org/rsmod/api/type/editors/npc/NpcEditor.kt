package org.rsmod.api.type.editors.npc

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.NpcPluginBuilder
import org.rsmod.game.type.npc.UnpackedNpcType

public abstract class NpcEditor : TypeEditor<NpcPluginBuilder, UnpackedNpcType>() {
    override fun edit(internal: String, init: NpcPluginBuilder.() -> Unit) {
        val type = NpcPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
