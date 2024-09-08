package org.rsmod.api.type.editors.loc

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.LocPluginBuilder
import org.rsmod.game.type.loc.UnpackedLocType

public abstract class LocEditor : TypeEditor<LocPluginBuilder, UnpackedLocType>() {
    override fun edit(internal: String, init: LocPluginBuilder.() -> Unit) {
        val type = LocPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
