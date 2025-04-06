package org.rsmod.api.db.sqlite

import com.google.common.util.concurrent.Service
import org.rsmod.api.db.Database
import org.rsmod.api.db.DatabaseConfig
import org.rsmod.module.ExtendedModule

public object SqliteModule : ExtendedModule() {
    override fun bind() {
        bind(DatabaseConfig::class.java).toInstance(DatabaseConfig.createSqlite())

        bindInstance<SqliteConnection>()
        bindBaseInstance<Database>(SqliteDatabase::class.java)
        addSetBinding<Service>(SqliteService::class.java)
    }
}
