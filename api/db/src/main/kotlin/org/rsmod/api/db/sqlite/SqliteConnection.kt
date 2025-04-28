package org.rsmod.api.db.sqlite

import jakarta.inject.Inject
import java.sql.Connection
import java.sql.DriverManager
import org.rsmod.api.db.DatabaseConfig

public class SqliteConnection @Inject constructor(private val config: DatabaseConfig) {
    public fun connect(): Connection {
        val connection = DriverManager.getConnection(config.url)
        connection.createStatement().use { statement ->
            statement.execute("PRAGMA foreign_keys = ON;")
            statement.execute("PRAGMA journal_mode = WAL;")
        }
        connection.autoCommit = false
        return connection
    }
}
