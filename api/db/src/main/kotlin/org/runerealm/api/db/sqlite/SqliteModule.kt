package org.runerealm.api.db.sqlite

import org.runerealm.api.db.Database
import org.runerealm.api.db.DatabaseConfig
import org.runerealm.module.ExtendedModule
import org.runerealm.server.services.Service

public object SqliteModule : ExtendedModule() {
    override fun bind() {
        bind(DatabaseConfig::class.java).toInstance(DatabaseConfig.createSqlite())

        bindInstance<SqliteConnection>()
        bindBaseAndImpl<Database>(SqliteDatabase::class.java)
        addSetBinding<Service>(SqliteService::class.java)
    }
}
