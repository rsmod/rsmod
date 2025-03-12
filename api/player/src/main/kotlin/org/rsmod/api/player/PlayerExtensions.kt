package org.rsmod.api.player

import org.rsmod.api.player.hit.configs.hit_queues
import org.rsmod.api.player.output.UpdateInventory
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.stat.hitpoints
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.inv.InvScope

public fun Player.forceDisconnect() {
    // TODO: disconnect player
    mes("TODO: Get Disconnected!")
}

public fun Player.clearInteractionRoute() {
    clearInteraction()
    abortRoute()
    clearMapFlag()
}

public fun Player.combatClearQueue() {
    clearQueue(hit_queues.standard)
    clearQueue(hit_queues.impact)
}

public fun Player.isValidTarget(): Boolean {
    return isSlotAssigned && isVisible && hitpoints > 0
}

public fun Player.startInvTransmit(inv: Inventory) {
    check(inv.type.scope != InvScope.Shared || !invMap.contains(inv.type)) {
        "`inv` should have previously been removed from cached inv map: $inv"
    }
    /*
     * Reorders the given `inv` in the list of transmitted inventories. This ensures that updates
     * for inventories are sent in the order they were added when this function was called, even if
     * they were first added during log in (e.g., `worn` and `inv`).
     *
     * This is done to emulate the behavior observed in os, where the transmitted inventory order
     * can change dynamically. For example, equipping an item will have the update order of `inv`
     * and `worn`. If you open a shop and then equip an item, the new order will be `worn` -> `inv`.
     *
     * This logic guarantees that updates sent from this point onward respect the new order.
     */
    transmittedInvs.remove(inv.type.id)
    transmittedInvAddQueue.add(inv.type.id)
    invMap[inv.type] = inv
}

public fun Player.stopInvTransmit(inv: Inventory) {
    if (inv.type.scope == InvScope.Shared) {
        val removed = invMap.remove(inv.type)
        check(removed == inv) { "Mismatch with cached value: (cached=$removed, inv=$inv)" }
    }
    transmittedInvs.remove(inv.type.id)
    transmittedInvAddQueue.remove(inv.type.id)
    UpdateInventory.updateInvStopTransmit(this, inv)
}
