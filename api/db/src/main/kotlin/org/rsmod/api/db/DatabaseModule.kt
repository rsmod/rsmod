package org.rsmod.api.db

import org.rsmod.api.db.migration.FlywayModule
import org.rsmod.api.db.sqlite.SqliteModule
import org.rsmod.module.ExtendedModule

public object DatabaseModule : ExtendedModule() {
    override fun bind() {
        install(FlywayModule)
        install(SqliteModule)
    }
}
