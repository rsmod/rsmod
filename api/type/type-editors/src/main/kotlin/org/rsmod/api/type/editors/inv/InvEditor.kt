package org.rsmod.api.type.editors.inv

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.InvPluginBuilder
import org.rsmod.game.type.inv.UnpackedInvType

public abstract class InvEditor : TypeEditor<InvPluginBuilder, UnpackedInvType>() {
    override fun edit(internal: String, init: InvPluginBuilder.() -> Unit) {
        val type = InvPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
