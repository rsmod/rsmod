package org.rsmod.api.npc

import org.rsmod.api.npc.hit.configs.hit_queues
import org.rsmod.game.entity.Npc

public fun Npc.combatClearQueue() {
    clearQueue(hit_queues.standard)
}
