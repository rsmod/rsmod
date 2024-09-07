package org.rsmod.api.type.editors.loc

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.game.type.loc.LocTypeBuilder
import org.rsmod.game.type.loc.UnpackedLocType

public abstract class LocEditor : TypeEditor<LocTypeBuilder, UnpackedLocType>() {
    override fun edit(internal: String, init: LocTypeBuilder.() -> Unit) {
        val type = LocTypeBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
