package org.runerealm.api.db

import org.runerealm.api.db.migration.FlywayModule
import org.runerealm.api.db.sqlite.SqliteModule
import org.runerealm.module.ExtendedModule

public object DatabaseModule : ExtendedModule() {
    override fun bind() {
        install(FlywayModule)
        install(SqliteModule)
    }
}
