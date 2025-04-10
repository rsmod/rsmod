package org.rsmod.api.db.sqlite

import org.rsmod.api.db.Database
import org.rsmod.api.db.DatabaseConfig
import org.rsmod.module.ExtendedModule
import org.rsmod.server.services.Service

public object SqliteModule : ExtendedModule() {
    override fun bind() {
        bind(DatabaseConfig::class.java).toInstance(DatabaseConfig.createSqlite())

        bindInstance<SqliteConnection>()
        bindBaseAndImpl<Database>(SqliteDatabase::class.java)
        addSetBinding<Service>(SqliteService::class.java)
    }
}
