package org.runerealm.api.npc

import org.runerealm.api.area.checker.AreaChecker
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.areas
import org.runerealm.api.config.refs.queues
import org.runerealm.api.config.refs.varns
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.npc.NpcMode

public fun Npc.clearInteractionRoute() {
    clearInteraction()
    abortRoute()
}

public fun Npc.queueDeath() {
    queue(queues.death, 1)
}

public fun Npc.isValidTarget(): Boolean {
    return isSlotAssigned && isVisible && isNotDelayed && hitpoints > 0
}

public fun Npc.isOutOfCombat(): Boolean = !isInCombat()

public fun Npc.isInCombat(): Boolean {
    if (vars[varns.lastattack] + constants.combat_activecombat_delay >= currentMapClock) {
        return true
    }
    return mode == NpcMode.OpPlayer2 || mode == NpcMode.ApPlayer2 || mode == NpcMode.PlayerEscape
}

/** @return `true` if the npc is **currently** in a multi-combat area. */
public fun Npc.mapMultiway(checker: AreaChecker): Boolean {
    return checker.inArea(areas.multiway, coords)
}
