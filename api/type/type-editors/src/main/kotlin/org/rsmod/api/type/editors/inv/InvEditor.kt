package org.rsmod.api.type.editors.inv

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.game.type.inv.InvTypeBuilder
import org.rsmod.game.type.inv.UnpackedInvType

public abstract class InvEditor : TypeEditor<InvTypeBuilder, UnpackedInvType>() {
    override fun edit(internal: String, init: InvTypeBuilder.() -> Unit) {
        val type = InvTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
