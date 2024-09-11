package org.rsmod.api.game.process.npc

import jakarta.inject.Inject
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.map.zone.ZoneKey

public class NpcZoneUpdateProcessor
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
}
