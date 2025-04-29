package org.rsmod.api.db.gateway.model

import org.rsmod.api.db.DatabaseConnection

public fun interface GameDbRequest<T> {
    public operator fun invoke(connection: DatabaseConnection): GameDbResult<T>
}
