package org.rsmod.api.utils.logging

public fun interface GameExceptionHandler {
    public fun handle(t: Throwable, msg: () -> Any?)
}
