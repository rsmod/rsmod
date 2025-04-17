package org.rsmod.api.type.editors.headbar

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.game.type.headbar.HeadbarType
import org.rsmod.game.type.headbar.HeadbarTypeBuilder
import org.rsmod.game.type.headbar.UnpackedHeadbarType

public abstract class HeadbarEditor : TypeEditor<UnpackedHeadbarType>() {
    public fun edit(type: HeadbarType, init: HeadbarTypeBuilder.() -> Unit) {
        val type = HeadbarTypeBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
