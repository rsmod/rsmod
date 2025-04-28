package org.rsmod.api.db.sqlite

import java.sql.Connection
import java.sql.SQLException
import kotlinx.coroutines.delay
import org.rsmod.api.db.Database
import org.rsmod.api.db.DatabaseConnection
import org.rsmod.api.db.util.DatabaseRollbackException

public class SqliteDatabase : Database {
    private lateinit var connection: Connection

    public fun connect(connector: SqliteConnection) {
        check(!::connection.isInitialized) { "Connection already initialized." }
        val connection = connector.connect()
        this.connection = connection
    }

    public fun close() {
        assertValidConnection()
        this.connection.close()
    }

    override suspend fun <T> withTransaction(
        attempts: Int,
        backoff: Long,
        block: (DatabaseConnection) -> T,
    ): T =
        withConnection(attempts, backoff) { connection ->
            val wrapped = DatabaseConnection(connection)
            try {
                val result = block(wrapped)
                connection.commit()
                result
            } catch (t: Throwable) {
                try {
                    connection.rollback()
                } catch (rollbackEx: Throwable) {
                    throw DatabaseRollbackException(t, rollbackEx)
                }
                throw t
            }
        }

    private suspend fun <T> withConnection(
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

    private fun assertValidConnection() {
        check(::connection.isInitialized) { "Connection was not initialized." }
        check(!connection.isClosed) { "Connection is closed." }
    }
}
