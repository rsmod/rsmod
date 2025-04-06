package org.rsmod.api.db.sqlite

import java.sql.Connection
import java.sql.SQLException
import kotlinx.coroutines.delay
import org.rsmod.api.db.Database

public class SqliteDatabase : Database {
    private lateinit var connection: Connection

    public fun setupConnection(connection: Connection) {
        check(!::connection.isInitialized) { "Connection already initialized." }
        this.connection = connection
    }

    public fun closeConnection() {
        assertValidConnection()
        this.connection.close()
    }

    override suspend fun <T> withConnection(
        attempts: Int,
        backoff: Long,
        block: (Connection) -> T,
    ): T {
        assertValidConnection()

        repeat(attempts - 1) {
            try {
                return block(connection)
            } catch (_: SQLException) {
                if (backoff > 0) {
                    delay(backoff)
                }
            }
        }

        return block(connection)
    }

    override fun isClosed(): Boolean = connection.isClosed

    private fun assertValidConnection() {
        check(::connection.isInitialized) { "Connection was never initialized." }
        check(!isClosed()) { "Connection is closed." }
    }
}
