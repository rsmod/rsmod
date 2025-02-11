package org.rsmod.api.utils.logging

import com.github.michaelbull.logging.InlineLogger

public class GameExceptionLogHandler : GameExceptionHandler {
    private val logger = InlineLogger()

    override fun handle(t: Throwable, msg: () -> Any?) {
        logger.error(t, msg)
    }
}
