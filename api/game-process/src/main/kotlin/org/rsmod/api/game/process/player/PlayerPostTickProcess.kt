package org.rsmod.api.game.process.player

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.rsmod.api.player.forceDisconnect
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.seq.EntitySeq
import org.rsmod.map.zone.ZoneKey

public class PlayerPostTickProcess
@Inject
constructor(
    private val playerList: PlayerList,
    private val playerRegistry: PlayerRegistry,
    private val zoneUpdates: PlayerZoneUpdateProcessor,
) {
    private val logger = InlineLogger()

    public fun process() {
        updateZoneRegistry()
        sendZoneUpdates()
        finalizePostTick()
    }

    private fun updateZoneRegistry() {
        for (player in playerList) {
            player.tryOrDisconnect {
                val currZone = ZoneKey.from(coords)
                val prevZone = lastProcessedZone
                if (currZone != prevZone) {
                    playerRegistry.change(this, prevZone, currZone)
                }
            }
        }
    }

    @Suppress("DeferredResultUnused")
    private fun sendZoneUpdates() = runBlocking {
        zoneUpdates.computeEnclosedBuffers()
        supervisorScope {
            for (player in playerList) {
                async { player.tryOrDisconnect { zoneUpdates.process(this) } }
            }
        }
        zoneUpdates.clearEnclosedBuffers()
        zoneUpdates.clearPendingZoneUpdates()
    }

    private fun finalizePostTick() {
        for (player in playerList) {
            player.tryOrDisconnect {
                flushClient()
                cleanUpPendingUpdates()
            }
        }
    }

    private fun Player.flushClient() {
        client.flush()
    }

    private fun Player.cleanUpPendingUpdates() {
        pendingSequence = EntitySeq.NULL
        pendingSpotanims.clear()
        appearance.clearRebuildFlag()
    }

    private inline fun Player.tryOrDisconnect(block: Player.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            forceDisconnect()
            logger.error(e) { "Error processing zone updates for player: $this." }
        } catch (e: NotImplementedError) {
            forceDisconnect()
            logger.error(e) { "Error processing zone updates for player: $this." }
        }
}
