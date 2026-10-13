package org.runerealm.api.type.editors.npc

import org.runerealm.api.type.editors.TypeEditor
import org.runerealm.api.type.script.dsl.NpcPluginBuilder
import org.runerealm.game.type.npc.NpcType
import org.runerealm.game.type.npc.UnpackedNpcType

public abstract class NpcEditor : TypeEditor<UnpackedNpcType>() {
    public fun edit(type: NpcType, init: NpcPluginBuilder.() -> Unit) {
        val type = NpcPluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
