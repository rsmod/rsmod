package org.runerealm.api.db.gateway.model

import org.runerealm.api.db.DatabaseConnection

public fun interface GameDbRequest<T> {
    public operator fun invoke(connection: DatabaseConnection): GameDbResult<T>
}
