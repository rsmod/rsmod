package org.rsmod.plugins.api.session

import com.github.michaelbull.logging.InlineLogger
import org.openrs2.crypto.XteaKey
import org.rsmod.game.client.Client
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.plugins.api.cache.map.xtea.XteaRepository
import org.rsmod.plugins.api.net.downstream.RebuildNormal
import org.rsmod.plugins.api.net.downstream.GPIInitialization
import javax.inject.Inject
import javax.inject.Singleton

private val logger = InlineLogger()

@Singleton
public class ClientGameSession @Inject constructor(
    private val players: PlayerList,
    private val xteaRepository: XteaRepository
) {

    public fun connect(client: Client) {
        val player = client.player
        val channel = client.channel
        val rebuildNormal = createRebuildNormal(player.index, player.coords)
        // REBUILD_NORMAL should be priority and the first packet to be
        // sent downstream.
        player.downstream.add(0, rebuildNormal)
        player.downstream.flush(channel)
        logger.debug { "Client connected: $client." }
    }

    public fun disconnect(client: Client) {
        logger.debug { "Client disconnected: $client." }
    }

    private fun createRebuildNormal(playerIndex: Int, playerCoords: Coordinates): RebuildNormal {
        val zone = playerCoords.toZone()
        val xtea = mutableListOf<Int>()
        zone.toViewport().forEach {
            val key = xteaRepository[it.id] ?: XteaKey.ZERO
            xtea += key.k0
            xtea += key.k1
            xtea += key.k2
            xtea += key.k3
        }
        val gpi = GPIInitialization(playerCoords.packed, players.playerCoords(excludeIndex = playerIndex))
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
