package org.runerealm.api.type.editors.dbrow

import org.runerealm.api.type.editors.TypeEditor
import org.runerealm.api.type.script.dsl.DbRowPluginBuilder
import org.runerealm.game.type.dbrow.DbRowType
import org.runerealm.game.type.dbrow.UnpackedDbRowType

public abstract class DbRowEditor : TypeEditor<UnpackedDbRowType>() {
    public fun edit(type: DbRowType, init: DbRowPluginBuilder.() -> Unit) {
        val type = DbRowPluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
