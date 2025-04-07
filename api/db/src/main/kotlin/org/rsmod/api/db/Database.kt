package org.rsmod.api.db

import java.sql.Connection

public interface Database {
    public suspend fun <T> withConnection(
        attempts: Int = 3,
        backoff: Long = 10L,
        block: (Connection) -> T,
    ): T
}
