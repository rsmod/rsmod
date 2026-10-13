package org.runerealm.api.type.editors.loc

import org.runerealm.api.type.editors.TypeEditor
import org.runerealm.api.type.script.dsl.LocPluginBuilder
import org.runerealm.game.type.loc.LocType
import org.runerealm.game.type.loc.UnpackedLocType

public abstract class LocEditor : TypeEditor<UnpackedLocType>() {
    public fun edit(type: LocType, init: LocPluginBuilder.() -> Unit) {
        val type = LocPluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
