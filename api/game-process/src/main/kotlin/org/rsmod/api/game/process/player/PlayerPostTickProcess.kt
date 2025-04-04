package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.rsmod.api.player.forceDisconnect
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.api.utils.logging.GameExceptionHandler
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.entity.util.EntityFaceAngle
import org.rsmod.game.seq.EntitySeq
import org.rsmod.map.zone.ZoneKey

public class PlayerPostTickProcess
@Inject
constructor(
    private val playerList: PlayerList,
    private val playerRegistry: PlayerRegistry,
    private val zoneUpdates: PlayerZoneUpdateProcessor,
    private val invUpdates: PlayerInvUpdateProcessor,
    private val statUpdates: PlayerStatUpdateProcessor,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        updateZoneRegistry()
        sendZoneUpdates()
        processPostTick()
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

    private fun processPostTick() {
        for (player in playerList) {
            player.tryOrDisconnect {
                updateTransmittedInvs()
                updatePendingStats()
                flushClient()
                cleanUpPendingUpdates()
            }
        }
    }

    private fun Player.updateTransmittedInvs() {
        invUpdates.process(this)
    }

    private fun Player.updatePendingStats() {
        statUpdates.process(this)
    }

    private fun Player.flushClient() {
        clientCycle.postCycle(this)
        client.flush()
    }

    private fun Player.cleanUpPendingUpdates() {
        pendingFaceAngle = EntityFaceAngle.NULL
        pendingSequence = EntitySeq.NULL
        pendingSpotanims.clear()
        activeHeadbars.clear()
        activeHitmarks.clear()
        appearance.clearRebuildFlag()
    }

    private fun finalizePostTick() {
        invUpdates.cleanUp()
    }

    private inline fun Player.tryOrDisconnect(block: Player.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            forceDisconnect()
            exceptionHandler.handle(e) { "Error processing post-tick for player: $this." }
        } catch (e: NotImplementedError) {
            forceDisconnect()
            exceptionHandler.handle(e) { "Error processing post-tick for player: $this." }
        }
}
