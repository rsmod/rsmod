package org.rsmod.api.utils.logging

import org.rsmod.module.ExtendedModule

public object ExceptionHandlerModule : ExtendedModule() {
    override fun bind() {
        bindBaseInstance<GameExceptionHandler, GameExceptionLogHandler>()
    }
}
