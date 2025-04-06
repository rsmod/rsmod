package org.rsmod.api.db.sqlite

import com.google.common.util.concurrent.AbstractService
import jakarta.inject.Inject
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import org.rsmod.api.db.DatabaseConfig
import org.rsmod.api.db.migration.FlywayMigration

public class SqliteService
@Inject
constructor(
    private val config: DatabaseConfig,
    private val migration: FlywayMigration,
    private val connection: SqliteConnection,
    private val database: SqliteDatabase,
) : AbstractService() {
    override fun doStart() {
        createNecessaryPaths()
        executeMigrations()
        connectDataSource()
    }

    private fun executeMigrations() {
        migration.migrate()
    }

    private fun connectDataSource() {
        val connection = connection.connect()
        database.setupConnection(connection)
    }

    override fun doStop() {
        database.closeConnection()
    }

    private fun createNecessaryPaths() {
        if (config.isMemoryMode()) {
            return
        }
        val filePath = config.resolveFilePath()
        filePath.createParentDirectories()
    }
}

private fun DatabaseConfig.isMemoryMode(): Boolean = path.startsWith(":memory:")

private fun DatabaseConfig.resolveFilePath(): Path = Path.of(path.removePrefix("file:"))
