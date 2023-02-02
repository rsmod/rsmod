package org.rsmod.plugins.api.session

import io.netty.channel.Channel
import org.openrs2.crypto.XteaKey
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.plugins.api.cache.map.xtea.XteaRepository
import org.rsmod.plugins.api.net.downstream.IfOpenTop
import org.rsmod.plugins.api.net.downstream.RebuildNormal
import org.rsmod.plugins.api.prot.info.GPIInitialization

public object GameSession {

    public fun connect(
        channel: Channel,
        player: Player,
        players: PlayerList,
        xteaRepository: XteaRepository
    ) {
        val rebuildNormal = createRebuildNormal(player.index, player.coords, players, xteaRepository)
        player.downstream += rebuildNormal
        player.downstream += IfOpenTop(161)
        player.downstream.flush(channel)
    }

    @Suppress("unused", "unused_parameter")
    public fun disconnect(channel: Channel, player: Player) {
    }

    private fun createRebuildNormal(
        playerIndex: Int,
        playerCoords: Coordinates,
        players: PlayerList,
        repository: XteaRepository
    ): RebuildNormal {
        val zone = playerCoords.toZone()
        val xtea = mutableListOf<Int>()
        zone.toViewport().forEach {
            val key = repository[it.id] ?: XteaKey.ZERO
            xtea += key.k0
            xtea += key.k1
            xtea += key.k2
            xtea += key.k3
        }
        val gpi = GPIInitialization(playerCoords.packed30Bits, players.playerCoords(excludeIndex = playerIndex))
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
