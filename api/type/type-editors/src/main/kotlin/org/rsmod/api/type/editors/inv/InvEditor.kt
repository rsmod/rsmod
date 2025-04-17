package org.rsmod.api.type.editors.inv

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.InvPluginBuilder
import org.rsmod.game.type.inv.InvType
import org.rsmod.game.type.inv.UnpackedInvType

public abstract class InvEditor : TypeEditor<UnpackedInvType>() {
    public fun edit(type: InvType, init: InvPluginBuilder.() -> Unit) {
        val type = InvPluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
