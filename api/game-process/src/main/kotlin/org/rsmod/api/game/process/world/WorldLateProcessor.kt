package org.rsmod.api.game.process.world

import jakarta.inject.Inject
import org.rsmod.api.game.process.npc.NpcZoneUpdateProcessor
import org.rsmod.api.game.process.player.PlayerInvUpdateProcessor
import org.rsmod.api.repo.EntityLifecycleProcess

public class WorldLateProcessor
@Inject
constructor(
    private val inventoryUpdates: PlayerInvUpdateProcessor,
    private val npcZoneUpdates: NpcZoneUpdateProcessor,
    private val entityLifecycle: EntityLifecycleProcess,
) {
    public fun process() {
        inventoryUpdates.process()
        npcZoneUpdates.process()
        entityLifecycle.process()
    }
}
