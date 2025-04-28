package org.rsmod.api.db

public interface Database {
    public suspend fun <T> withTransaction(
        attempts: Int = 3,
        backoff: Long = 10L,
        block: (DatabaseConnection) -> T,
    ): T
}
