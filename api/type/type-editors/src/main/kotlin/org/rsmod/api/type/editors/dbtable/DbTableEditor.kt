package org.rsmod.api.type.editors.dbtable

import org.rsmod.api.type.editors.TypeEditor
import org.rsmod.api.type.script.dsl.DbTablePluginBuilder
import org.rsmod.game.type.dbtable.DbTableType
import org.rsmod.game.type.dbtable.UnpackedDbTableType

public abstract class DbTableEditor : TypeEditor<UnpackedDbTableType>() {
    public fun edit(type: DbTableType, init: DbTablePluginBuilder.() -> Unit) {
        val type = DbTablePluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
