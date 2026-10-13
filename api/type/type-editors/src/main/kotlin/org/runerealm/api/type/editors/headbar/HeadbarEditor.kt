package org.runerealm.api.type.editors.headbar

import org.runerealm.api.type.editors.TypeEditor
import org.runerealm.game.type.headbar.HeadbarType
import org.runerealm.game.type.headbar.HeadbarTypeBuilder
import org.runerealm.game.type.headbar.UnpackedHeadbarType

public abstract class HeadbarEditor : TypeEditor<UnpackedHeadbarType>() {
    public fun edit(type: HeadbarType, init: HeadbarTypeBuilder.() -> Unit) {
        val type = HeadbarTypeBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
