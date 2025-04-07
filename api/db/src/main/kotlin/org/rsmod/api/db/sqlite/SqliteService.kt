package org.rsmod.api.db.sqlite

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import org.rsmod.api.db.DatabaseConfig
import org.rsmod.api.db.migration.FlywayMigration
import org.rsmod.server.services.Service

public class SqliteService
@Inject
constructor(
    private val config: DatabaseConfig,
    private val migration: FlywayMigration,
    private val connection: SqliteConnection,
    private val database: SqliteDatabase,
) : Service {
    private val logger = InlineLogger()

    override suspend fun startup() {
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

    override suspend fun shutdown() {
        logger.info { "Attempting to shut down sqlite service." }
        try {
            database.closeConnection()
            logger.info { "Sqlite service successfully shut down." }
        } catch (t: Throwable) {
            logger.error(t) { "Sqlite service failed to shut down." }
        }
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
