package org.rsmod.api.net.rsprot.provider

import java.lang.Exception
import net.rsprot.protocol.api.suppliers.WorldEntityInfoSupplier
import net.rsprot.protocol.game.outgoing.info.worldentityinfo.WorldEntityAvatarExceptionHandler
import net.rsprot.protocol.game.outgoing.info.worldentityinfo.WorldEntityIndexSupplier

object WorldEntityProvider {
    fun provide(): WorldEntityInfoSupplier {
        return WorldEntityInfoSupplier(IndexSupplier, ExceptionHandler)
    }

    private object IndexSupplier : WorldEntityIndexSupplier {
        override fun supply(
            localPlayerIndex: Int,
            level: Int,
            x: Int,
            z: Int,
            viewDistance: Int,
        ): Iterator<Int> = emptyList<Int>().iterator()
    }

    private object ExceptionHandler : WorldEntityAvatarExceptionHandler {
        override fun exceptionCaught(index: Int, exception: Exception) {
            /* no-op */
        }
    }
}
