package org.rsmod.api.db.gateway.model

public fun interface GameDbResponse<T> {
    public operator fun invoke(result: GameDbResult<T>)
}
