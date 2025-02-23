package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.api.utils.logging.GameExceptionHandler
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.face.EntityFaceAngle
import org.rsmod.game.seq.EntitySeq
import org.rsmod.map.zone.ZoneKey

public class NpcPostTickProcess
@Inject
constructor(
    private val npcList: NpcList,
    private val registry: NpcRegistry,
    private val exceptionHandler: GameExceptionHandler,
) {
    public fun process() {
        for (npc in npcList) {
            npc.tryOrDespawn { process() }
        }
    }

    private fun Npc.process() {
        if (hasMovedThisCycle) {
            processZoneUpdates()
        }
        cleanUpPendingUpdates()
    }

    private fun Npc.processZoneUpdates() {
        val oldZone = lastProcessedZone
        val newZone = ZoneKey.from(coords)
        if (oldZone == newZone) {
            return
        }
        registry.change(this, oldZone, newZone)
        lastProcessedZone = newZone
    }

    private fun Npc.cleanUpPendingUpdates() {
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
