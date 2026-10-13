package org.runerealm.api.type.editors.obj

import org.runerealm.api.type.editors.TypeEditor
import org.runerealm.api.type.script.dsl.ObjPluginBuilder
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.obj.UnpackedObjType

public abstract class ObjEditor : TypeEditor<UnpackedObjType>() {
    public fun edit(type: ObjType, init: ObjPluginBuilder.() -> Unit) {
        val type = ObjPluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
