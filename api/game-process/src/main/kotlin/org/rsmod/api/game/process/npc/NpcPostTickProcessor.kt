package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.seq.EntitySeq
import org.rsmod.map.zone.ZoneKey

public class NpcPostTickProcessor
@Inject
constructor(private val npcList: NpcList, private val registry: NpcRegistry) {
    public fun process() {
        for (npc in npcList) {
            process(npc)
        }
    }

    public fun process(npc: Npc) {
        if (npc.hasMovedThisCycle) {
            npc.processZoneUpdates()
        }
        npc.cleanUpPendingUpdates()
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
        pendingSequence = EntitySeq.NULL
    }
}
