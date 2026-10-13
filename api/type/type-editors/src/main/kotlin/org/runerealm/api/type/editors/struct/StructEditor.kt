package org.runerealm.api.type.editors.struct

import org.runerealm.api.type.editors.TypeEditor
import org.runerealm.api.type.script.dsl.StructPluginBuilder
import org.runerealm.game.type.struct.StructType
import org.runerealm.game.type.struct.UnpackedStructType

public abstract class StructEditor : TypeEditor<UnpackedStructType>() {
    public fun edit(type: StructType, init: StructPluginBuilder.() -> Unit) {
        val type = StructPluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
