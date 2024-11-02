package org.rsmod.api.net.rsprot.provider

import java.lang.Exception
import net.rsprot.protocol.api.suppliers.NpcInfoSupplier
import net.rsprot.protocol.game.outgoing.info.npcinfo.NpcAvatarExceptionHandler

object NpcSupplier {
    fun provide(): NpcInfoSupplier = NpcInfoSupplier(ExceptionHandler)

    private object ExceptionHandler : NpcAvatarExceptionHandler {
        override fun exceptionCaught(index: Int, exception: Exception) {
            exception.printStackTrace()
        }
    }
}
