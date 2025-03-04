package org.rsmod.api.type.editors.headbar

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.game.type.headbar.HeadbarTypeBuilder
import org.rsmod.game.type.headbar.UnpackedHeadbarType

public abstract class HeadbarEditor : TypeEditor<HeadbarTypeBuilder, UnpackedHeadbarType>() {
    override fun edit(internal: String, init: HeadbarTypeBuilder.() -> Unit) {
        val type = HeadbarTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
