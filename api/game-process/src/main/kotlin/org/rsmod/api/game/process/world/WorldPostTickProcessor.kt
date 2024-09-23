package org.rsmod.api.game.process.world

import jakarta.inject.Inject
import org.rsmod.api.game.process.npc.NpcPostTickProcessor
import org.rsmod.api.game.process.player.PlayerInvUpdateProcessor
import org.rsmod.api.repo.EntityLifecycleProcess

public class WorldPostTickProcessor
@Inject
constructor(
    private val inventoryUpdates: PlayerInvUpdateProcessor,
    private val npcPostTick: NpcPostTickProcessor,
    private val entityLifecycle: EntityLifecycleProcess,
) {
    public fun process() {
        inventoryUpdates.process()
        npcPostTick.process()
        entityLifecycle.process()
    }
}
