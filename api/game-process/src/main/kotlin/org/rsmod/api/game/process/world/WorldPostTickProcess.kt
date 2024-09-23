package org.rsmod.api.game.process.world

import jakarta.inject.Inject
import org.rsmod.api.game.process.npc.NpcPostTickProcess
import org.rsmod.api.game.process.player.PlayerInvUpdateProcess
import org.rsmod.api.repo.EntityLifecycleProcess

public class WorldPostTickProcess
@Inject
constructor(
    private val invUpdates: PlayerInvUpdateProcess,
    private val npcPostTick: NpcPostTickProcess,
    private val entityLifecycle: EntityLifecycleProcess,
) {
    public fun process() {
        invUpdates.process()
        npcPostTick.process()
        entityLifecycle.process()
    }
}
