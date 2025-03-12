package org.rsmod.api.npc

import org.rsmod.api.config.refs.queues
import org.rsmod.api.npc.hit.configs.hit_queues
import org.rsmod.game.entity.Npc

public fun Npc.queueDeath() {
    queue(queues.death, 1)
}

public fun Npc.combatClearQueue() {
    clearQueue(hit_queues.standard)
}

public fun Npc.isValidTarget(): Boolean {
    return isSlotAssigned && isVisible && isNotDelayed && hitpoints > 0
}
