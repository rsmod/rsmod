package org.rsmod.api.db

public interface Database {
    public suspend fun <T> withTransaction(block: (DatabaseConnection) -> T): T
}
