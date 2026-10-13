package org.runerealm.api.type.builders.dbtable

import org.runerealm.api.type.builders.HashTypeBuilder
import org.runerealm.api.type.script.dsl.DbTablePluginBuilder
import org.runerealm.game.type.dbtable.UnpackedDbTableType

public abstract class DbTableBuilder :
    HashTypeBuilder<DbTablePluginBuilder, UnpackedDbTableType>() {
    override fun build(internal: String, init: DbTablePluginBuilder.() -> Unit) {
        val type = DbTablePluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
