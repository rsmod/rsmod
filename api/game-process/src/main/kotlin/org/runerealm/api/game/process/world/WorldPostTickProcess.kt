package org.runerealm.api.game.process.world

import jakarta.inject.Inject
import org.runerealm.api.game.process.npc.NpcPostTickProcess
import org.runerealm.api.repo.EntityLifecycleProcess

public class WorldPostTickProcess
@Inject
constructor(
    private val npcPostTick: NpcPostTickProcess,
    private val entityLifecycle: EntityLifecycleProcess,
    private val update: WorldUpdateProcess,
) {
    public fun process() {
        npcPostTick.process()
        entityLifecycle.process()
        update.process()
    }
}
