package org.rsmod.api.db.gateway.model

import kotlin.contracts.contract
import org.rsmod.api.db.gateway.model.GameDbResult.Err
import org.rsmod.api.db.gateway.model.GameDbResult.Ok

public sealed class GameDbResult<out T> {
    public data class Ok<T>(val value: T) : GameDbResult<T>()

    public sealed class Err : GameDbResult<Nothing>() {
        public data class Exception(val cause: Throwable) : Err()

        public data object Timeout : Err()

        public data object InternalShutdownError : Err()
    }
}

public fun <T> GameDbResult<T>.isOk(): Boolean {
    contract {
        returns(true) implies (this@isOk is Ok)
        returns(false) implies (this@isOk is Err)
    }
    return this is Ok
}

public fun <T> GameDbResult<T>.isErr(): Boolean {
    contract {
        returns(true) implies (this@isErr is Err)
        returns(false) implies (this@isErr is Ok)
    }
    return this is Err
}

public inline fun <T, R> GameDbResult<T>.fold(onOk: (T) -> R, onErr: (Err) -> R): R =
    when (this) {
        is Ok -> onOk(value)
        is Err -> onErr(this)
    }
