package org.rsmod.api.type.builders.dbrow

import org.rsmod.api.type.builders.HashTypeBuilder
import org.rsmod.api.type.script.dsl.DbRowPluginBuilder
import org.rsmod.game.type.dbrow.UnpackedDbRowType

public abstract class DbRowBuilder : HashTypeBuilder<DbRowPluginBuilder, UnpackedDbRowType>() {
    override fun build(internal: String, init: DbRowPluginBuilder.() -> Unit) {
        val type = DbRowPluginBuilder(internal).apply(init).build(id = -1)
        cache += type
    }
}
