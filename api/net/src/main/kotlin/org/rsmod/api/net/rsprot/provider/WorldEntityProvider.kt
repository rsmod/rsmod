package org.rsmod.api.net.rsprot.provider

import java.lang.Exception
import net.rsprot.protocol.api.suppliers.WorldEntityInfoSupplier
import net.rsprot.protocol.game.outgoing.info.worldentityinfo.WorldEntityAvatarExceptionHandler

object WorldEntityProvider {
    fun provide(): WorldEntityInfoSupplier {
        return WorldEntityInfoSupplier(ExceptionHandler)
    }

    private object ExceptionHandler : WorldEntityAvatarExceptionHandler {
        override fun exceptionCaught(index: Int, exception: Exception) {
            /* no-op */
        }
    }
}
