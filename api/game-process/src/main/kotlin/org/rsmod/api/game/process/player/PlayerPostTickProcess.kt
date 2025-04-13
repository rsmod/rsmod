package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.rsmod.api.game.process.GameLifecycle
import org.rsmod.api.player.forceDisconnect
import org.rsmod.api.player.output.MiscOutput
import org.rsmod.api.registry.account.AccountRegistry
import org.rsmod.api.utils.logging.GameExceptionHandler
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.entity.util.EntityFaceAngle
import org.rsmod.game.seq.EntitySeq

public class PlayerPostTickProcess
@Inject
constructor(
    private val eventBus: EventBus,
    private val playerList: PlayerList,
    private val accountRegistry: AccountRegistry,
    private val zoneUpdates: PlayerZoneUpdateProcessor,
    private val buildAreas: PlayerBuildAreaProcessor,
    private val regions: PlayerRegionProcessor,
    private val facing: PlayerFaceSquareProcessor,
    private val mapUpdates: PlayerMapUpdateProcessor,
    private val invUpdates: PlayerInvUpdateProcessor,
    private val statUpdates: PlayerStatUpdateProcessor,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        computeSharedBuffers()
        updateProtocolInfo()
        processClientOutAsync()
        processPostTick()
        finalizePostTick()
    }

    private fun computeSharedBuffers() {
        zoneUpdates.computeEnclosedBuffers()
    }

    private fun updateProtocolInfo() {
        for (player in playerList) {
            player.tryOrDisconnect {
                facing.process(this)
                regions.process(this)
                buildAreas.process(this)
                clientCycle.update(this)
            }
        }
        eventBus.publish(GameLifecycle.UpdateInfo)
    }

    private fun processClientOutAsync() = runBlocking {
        supervisorScope {
            for (player in playerList) {
                async {
                    player.tryOrDisconnect {
                        clientCycle.flush(this)
                        zoneUpdates.process(this)
                    }
                }
            }
        }
    }

    // Ideally, this entire loop would be part of `processClientOutAsync`, but inventory updates
    // are currently not thread-safe. This is due to shared inventories requiring mutation: they
    // must be added to a collection and have their "modified slots" cleared afterward.
    // It is not a major issue for now, but we should eventually resolve this so the two loops
    // can be combined under the async processing phase.
    private fun processPostTick() {
        for (player in playerList) {
            player.tryOrDisconnect {
                processDisconnection()
                processMapChanges()
                processInvUpdates()
                processStatUpdates()
                flushClient()
                cleanUpPendingUpdates()
            }
        }
    }

    private fun Player.processDisconnection() {
        checkForcedDisconnect()
        if (!disconnected.get()) {
            disconnectedCycles = 0
            return
        }
        countDisconnectedCycle()
    }

    private fun Player.checkForcedDisconnect() {
        if (forceDisconnect && !pendingLogout) {
            queueLogout()
            closeClient()
            forceDisconnect = false
        }
    }

    private fun Player.countDisconnectedCycle() {
        if (!pendingLogout) {
            if (disconnectedCycles == RECONNECT_GRACE_PERIOD) {
                queueLogout()
            }
            disconnectedCycles++
        }
    }

    private fun Player.queueLogout() {
        pendingLogout = true
        accountRegistry.queueLogout(this)
    }

    private fun Player.closeClient() {
        MiscOutput.logout(this)
        client.close()
    }

    private fun Player.processMapChanges() {
        mapUpdates.process(this)
    }

    private fun Player.processInvUpdates() {
        invUpdates.process(this)
    }

    private fun Player.processStatUpdates() {
        statUpdates.process(this)
    }

    private fun Player.flushClient() {
        MiscOutput.serverTickEnd(this)
        client.flush()
    }

    private fun Player.cleanUpPendingUpdates() {
        pendingStepCount = 0
        pendingTeleport = false
        pendingTelejump = false
        pendingFaceAngle = EntityFaceAngle.NULL
        pendingSequence = EntitySeq.NULL
        pendingSpotanims.clear()
        activeHeadbars.clear()
        activeHitmarks.clear()
        appearance.clearRebuildFlag()
    }

    private fun finalizePostTick() {
        zoneUpdates.clearEnclosedBuffers()
        zoneUpdates.clearPendingZoneUpdates()
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

    private companion object {
        /**
         * The grace period (in server cycles) during which a disconnected player is allowed to
         * remain in the world before their logout is queued. This gives them a chance to reconnect
         * in time, based on this constant.
         */
        private const val RECONNECT_GRACE_PERIOD: Int = 10
    }
}
