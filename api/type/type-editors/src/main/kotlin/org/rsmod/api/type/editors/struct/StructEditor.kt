package org.rsmod.api.type.editors.struct

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.StructPluginBuilder
import org.rsmod.game.type.struct.StructType
import org.rsmod.game.type.struct.UnpackedStructType

public abstract class StructEditor : TypeEditor<UnpackedStructType>() {
    public fun edit(type: StructType, init: StructPluginBuilder.() -> Unit) {
        val type = StructPluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
