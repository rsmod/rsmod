package org.runerealm.api.type.editors.dbtable

import org.runerealm.api.type.editors.TypeEditor
import org.runerealm.api.type.script.dsl.DbTablePluginBuilder
import org.runerealm.game.type.dbtable.DbTableType
import org.runerealm.game.type.dbtable.UnpackedDbTableType

public abstract class DbTableEditor : TypeEditor<UnpackedDbTableType>() {
    public fun edit(type: DbTableType, init: DbTablePluginBuilder.() -> Unit) {
        val type = DbTablePluginBuilder(type.internalNameValue).apply(init).build(id = -1)
        cache += type
    }
}
