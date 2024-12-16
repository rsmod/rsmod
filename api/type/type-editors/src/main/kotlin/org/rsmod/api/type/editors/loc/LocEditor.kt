package org.rsmod.api.type.editors.loc

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.LocPluginBuilder
import org.rsmod.game.type.loc.UnpackedLocType

public abstract class LocEditor : TypeEditor<LocPluginBuilder, UnpackedLocType>() {
    override fun edit(internal: String, init: LocPluginBuilder.() -> Unit) {
        val type = LocPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }

    public fun edit(
        first: String,
        second: String,
        vararg rest: String,
        init: LocPluginBuilder.() -> Unit,
    ) {
        edit(first, init)
        edit(second, init)
        rest.forEach { edit(it, init) }
    }
}
