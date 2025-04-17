package org.rsmod.api.type.editors.loc

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.LocPluginBuilder
import org.rsmod.game.type.loc.LocType
import org.rsmod.game.type.loc.UnpackedLocType

public abstract class LocEditor : TypeEditor<UnpackedLocType>() {
    public fun edit(type: LocType, init: LocPluginBuilder.() -> Unit) {
        val type = LocPluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
