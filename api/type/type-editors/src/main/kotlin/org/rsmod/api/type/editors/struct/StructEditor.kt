package org.rsmod.api.type.editors.struct

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.StructPluginBuilder
import org.rsmod.game.type.struct.UnpackedStructType

public abstract class StructEditor : TypeEditor<StructPluginBuilder, UnpackedStructType>() {
    override fun edit(internal: String, init: StructPluginBuilder.() -> Unit) {
        val type = StructPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
