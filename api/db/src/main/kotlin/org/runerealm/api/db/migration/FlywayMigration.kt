package org.runerealm.api.db.migration

import jakarta.inject.Inject
import org.flywaydb.core.Flyway
import org.runerealm.api.db.DatabaseConfig

public class FlywayMigration @Inject constructor(private val config: DatabaseConfig) {
    public fun migrate() {
        val flyway =
            Flyway.configure()
                .dataSource(config.url, config.user, config.password)
                .locations("classpath:db/migration", "classpath:plugin/**/migration")
                .load()
        flyway.migrate()
    }
}
