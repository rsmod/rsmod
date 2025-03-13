package org.rsmod.api.npc

import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.varns
import org.rsmod.api.npc.interact.AiPlayerInteractions
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.npc.NpcMode
import org.rsmod.game.interact.InteractionOp

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

public fun Npc.isInCombat(): Boolean {
    if (vars[varns.lastattack] + constants.combat_default_attackrate >= currentMapClock) {
        return true
    }
    return mode == NpcMode.OpPlayer2 || mode == NpcMode.ApPlayer2 || mode == NpcMode.PlayerEscape
}

public fun Npc.isOutOfCombat(): Boolean = !isInCombat()

public fun Npc.opPlayer2(target: Player, interactions: AiPlayerInteractions) {
    opPlayer(target, NpcMode.OpPlayer2)
    interactions.interact(this, target, InteractionOp.Op2)
}

private fun Npc.opPlayer(target: Player, mode: NpcMode) {
    resetMovement()

    this.mode = mode
    facePlayer(target)
}
