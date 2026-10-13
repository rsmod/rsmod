package org.runerealm.api.utils.logging

import org.runerealm.module.ExtendedModule

public object ExceptionHandlerModule : ExtendedModule() {
    override fun bind() {
        bindBaseInstance<GameExceptionHandler>(GameExceptionLogHandler::class.java)
    }
}
