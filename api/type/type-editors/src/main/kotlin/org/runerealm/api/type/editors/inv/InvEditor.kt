package org.runerealm.api.type.editors.inv

import org.runerealm.api.type.editors.TypeEditor
import org.runerealm.api.type.script.dsl.InvPluginBuilder
import org.runerealm.game.type.inv.InvType
import org.runerealm.game.type.inv.UnpackedInvType

public abstract class InvEditor : TypeEditor<UnpackedInvType>() {
    public fun edit(type: InvType, init: InvPluginBuilder.() -> Unit) {
        val type = InvPluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
