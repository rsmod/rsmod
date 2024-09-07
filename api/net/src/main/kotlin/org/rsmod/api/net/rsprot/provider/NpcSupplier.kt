package org.rsmod.api.net.rsprot.provider

import java.lang.Exception
import net.rsprot.protocol.api.suppliers.NpcInfoSupplier
import net.rsprot.protocol.game.outgoing.info.npcinfo.NpcAvatarExceptionHandler
import net.rsprot.protocol.game.outgoing.info.npcinfo.NpcIndexSupplier
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.PlayerList

object NpcSupplier {
    fun provide(players: PlayerList, npcs: NpcList): NpcInfoSupplier =
        NpcInfoSupplier(IndexSupplier(players, npcs), ExceptionHandler)

    private class IndexSupplier(private val players: PlayerList, private val npcs: NpcList) :
        NpcIndexSupplier {
        private val slots = hashSetOf<Int>()

        override fun supply(
            localPlayerIndex: Int,
            level: Int,
            x: Int,
            z: Int,
            viewDistance: Int,
        ): Iterator<Int> {
            val player = players[localPlayerIndex] ?: return emptyList<Int>().iterator()
            slots.clear()
            for (npc in npcs) {
                if (npc.isWithinDistance(player, 15)) {
                    slots += npc.slotId
                }
            }
            return slots.iterator()
        }
    }

    private object ExceptionHandler : NpcAvatarExceptionHandler {
        override fun exceptionCaught(index: Int, exception: Exception) {
            exception.printStackTrace()
        }
    }
}
