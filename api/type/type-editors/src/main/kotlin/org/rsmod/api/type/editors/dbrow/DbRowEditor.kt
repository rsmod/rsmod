package org.rsmod.api.type.editors.dbrow

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.DbRowPluginBuilder
import org.rsmod.game.type.dbrow.DbRowType
import org.rsmod.game.type.dbrow.UnpackedDbRowType

public abstract class DbRowEditor : TypeEditor<UnpackedDbRowType>() {
    public fun edit(type: DbRowType, init: DbRowPluginBuilder.() -> Unit) {
        val type = DbRowPluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
