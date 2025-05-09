package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.api.utils.logging.GameExceptionHandler
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.util.EntityFaceAngle
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.seq.EntitySeq
import org.rsmod.map.zone.ZoneKey

public class NpcPostTickProcess
@Inject
constructor(
    private val npcList: NpcList,
    private val registry: NpcRegistry,
    private val facing: NpcFaceSquareProcessor,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        for (npc in npcList) {
            npc.tryOrDespawn {
                processZoneUpdates()
                updateProtocolInfo()
                cleanUpPendingUpdates()
            }
        }
    }

    private fun Npc.processZoneUpdates() {
        if (!hasMovedThisCycle && !pendingTelejump && !pendingTeleport) {
            return
        }
        val oldZone = lastProcessedZone
        val newZone = ZoneKey.from(coords)
        if (oldZone == newZone) {
            return
        }
        registry.change(this, oldZone, newZone)
        lastProcessedZone = newZone
    }

    private fun Npc.updateProtocolInfo() {
        updateMovement()
        updateExactMove()
        updateFaceAngle()
    }

    private fun Npc.updateMovement() {
        if (pendingTelejump) {
            updateTelejump()
            return
        }

        if (pendingTeleport) {
            updateTeleport()
            return
        }

        if (!hasMovedThisCycle) {
            return
        }

        // This check ensures the npc did not move more than 2 tiles without using the appropriate
        // telejump/teleport functions. While we could call `updateTelejump` here to recover, that
        // risks subtle bugs - the teleport functions handle important side effects like updating
        // collision flags for the previous and current coordinates.
        val delta = coords.chebyshevDistance(previousCoords)
        check(delta in 0..2) {
            "Expected movement to be within 2-tile distance: " +
                "distance=$delta, previous=$previousCoords, curr=$coords"
        }

        when (pendingStepCount) {
            0 -> updateCrawlHalfStep()
            1 -> {
                if (moveSpeed == MoveSpeed.Crawl) {
                    updateCrawlHalfStep()
                } else {
                    updateWalkStep()
                }
            }
            2 -> updateRunStep()
            else -> throw NotImplementedError("Unhandled step count: $pendingStepCount")
        }
    }

    private fun Npc.updateCrawlHalfStep() {
        val deltaX = coords.x - previousCoords.x
        val deltaZ = coords.z - previousCoords.z
        infoProtocol.crawl(deltaX, deltaZ)
    }

    private fun Npc.updateWalkStep() {
        val deltaX = coords.x - previousCoords.x
        val deltaZ = coords.z - previousCoords.z
        infoProtocol.walk(deltaX, deltaZ)
    }

    private fun Npc.updateRunStep() {
        val intermediate = lastProcessedCoord

        val firstDeltaX = intermediate.x - previousCoords.x
        val firstDeltaZ = intermediate.z - previousCoords.z
        val secondDeltaX = coords.x - intermediate.x
        val secondDeltaZ = coords.z - intermediate.z

        infoProtocol.run(firstDeltaX, firstDeltaZ, secondDeltaX, secondDeltaZ)
    }

    private fun Npc.updateTeleport() {
        infoProtocol.teleport(x, z, level, jump = false)
    }

    private fun Npc.updateTelejump() {
        infoProtocol.teleport(x, z, level, jump = true)
    }

    private fun Npc.updateExactMove() {
        val pending = pendingExactMove ?: return
        infoProtocol.exactMove(
            deltaX1 = pending.deltaX1,
            deltaZ1 = pending.deltaZ1,
            deltaX2 = pending.deltaX2,
            deltaZ2 = pending.deltaZ2,
            delay1 = pending.clientDelay1,
            delay2 = pending.clientDelay2,
            direction = pending.direction,
        )
    }

    private fun Npc.updateFaceAngle() {
        val pending = pendingFaceSquare
        facing.process(this)
        if (pendingFaceAngle != EntityFaceAngle.NULL) {
            infoProtocol.setFaceSquare(pending.x, pending.z, instant = false)
        }
    }

    private fun Npc.cleanUpPendingUpdates() {
        pendingStepCount = 0
        pendingTeleport = false
        pendingTelejump = false
        pendingExactMove = null
        pendingFaceAngle = EntityFaceAngle.NULL
        pendingSequence = EntitySeq.NULL
        pendingSpotanims.clear()
    }

    private inline fun Npc.tryOrDespawn(block: Npc.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            registry.del(this)
            exceptionHandler.handle(e) { "Error processing post-tick for npc: $this." }
        } catch (e: NotImplementedError) {
            registry.del(this)
            exceptionHandler.handle(e) { "Error processing post-tick for npc: $this." }
        }
}
