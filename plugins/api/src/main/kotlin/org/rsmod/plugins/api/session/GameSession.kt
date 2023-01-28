package org.rsmod.plugins.api.session

import org.openrs2.crypto.XteaKey
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.plugins.api.cache.map.xtea.XteaRepository
import org.rsmod.plugins.api.event.PlayerSession
import org.rsmod.plugins.api.prot.GPIInitialization
import org.rsmod.plugins.api.prot.downstream.IfOpenTop
import org.rsmod.plugins.api.prot.downstream.RebuildNormal

object GameSession {

    fun connect(
        session: PlayerSession.Connected,
        players: PlayerList,
        xteaRepository: XteaRepository
    ) {
        val channel = session.channel
        val rebuildNormal = createRebuildNormal(players, xteaRepository)
        channel.write(rebuildNormal)
        channel.write(IfOpenTop(161))
        channel.flush()
    }

    private fun createRebuildNormal(players: PlayerList, repository: XteaRepository): RebuildNormal {
        val coords = Coordinates(3200, 3200)
        val zone = coords.toZone()
        val xtea = mutableListOf<Int>()
        zone.toViewport().forEach {
            val key = repository[it.id] ?: XteaKey.ZERO
            xtea += key.k0
            xtea += key.k1
            xtea += key.k2
            xtea += key.k3
        }
        val gpi = GPIInitialization(coords.packed30Bits, players.playerCoords(excludeIndex = 1))
        return RebuildNormal(gpi, zone, xtea)
    }

    private fun PlayerList.playerCoords(excludeIndex: Int): List<Int> = mapIndexedNotNull { index, player ->
        if (index != excludeIndex) {
            player?.coords?.packed18Bits ?: 0
        } else {
            null
        }
    }
}
