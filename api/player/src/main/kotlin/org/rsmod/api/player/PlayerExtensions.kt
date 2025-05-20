package org.rsmod.api.player

import org.rsmod.api.area.checker.AreaChecker
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.areas
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.timers
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.hit.configs.hit_queues
import org.rsmod.api.player.output.UpdateInventory
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.stat.hitpoints
import org.rsmod.api.player.vars.enabledPrayers
import org.rsmod.api.player.vars.prayerDrainCounter
import org.rsmod.api.player.vars.usingQuickPrayers
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.inv.InvScope

public fun Player.forceDisconnect() {
    forceDisconnect = true
}

public fun Player.clearInteractionRoute() {
    clearInteraction()
    abortRoute()
    clearMapFlag()
}

public fun Player.queueDeath() {
    queue(queues.death, 1)
}

public fun Player.combatClearQueue() {
    clearQueue(queues.com_retaliate_npc)
    clearQueue(queues.com_retaliate_player)
    clearQueue(hit_queues.standard)
    clearQueue(hit_queues.impact)
}

public fun Player.disablePrayers() {
    enabledPrayers = 0
    prayerDrainCounter = 0

    if (usingQuickPrayers) {
        usingQuickPrayers = false
    }

    if (constants.isOverhead(appearance.overheadIcon)) {
        appearance.overheadIcon = null
    }

    clearQueue(queues.preserve_activation)
    clearSoftTimer(timers.prayer_drain)
    clearSoftTimer(timers.rapidrestore_regen)
}

public fun Player.deathResetTimers() {
    softTimer(timers.stat_regen, constants.stat_regen_interval)
    softTimer(timers.stat_boost_restore, constants.stat_boost_restore_interval)
    softTimer(timers.health_regen, constants.health_regen_interval)

    // Note: RL regeneration meter plugin does not reset on death. This can lead to de-sync, but
    // it is (currently) the official behavior.
    softTimer(timers.spec_regen, constants.spec_regen_interval)
}

public fun Player.isValidTarget(): Boolean {
    val isLoggingOut = pendingLogout || loggingOut
    if (isLoggingOut) {
        return false
    }
    return isSlotAssigned && isVisible && hitpoints > 0
}

public fun Player.isOutOfCombat(): Boolean = !isInCombat()

public fun Player.isInCombat(): Boolean = isInPvpCombat() || isInPvnCombat()

public fun Player.isInPvpCombat(): Boolean {
    return vars[varps.lastcombat_pvp] + constants.combat_activecombat_delay >= currentMapClock
}

public fun Player.isInPvnCombat(): Boolean {
    return vars[varps.lastcombat] + constants.combat_activecombat_delay >= currentMapClock
}

/** @return `true` if the player is **currently** in a multi-combat area. */
public fun Player.mapMultiway(checker: AreaChecker): Boolean {
    return checker.inArea(areas.multiway, coords)
}

public fun Player.startInvTransmit(inv: Inventory) {
    check(inv.type.scope != InvScope.Shared || !invMap.contains(inv.type)) {
        "`inv` should have previously been removed from cached inv map: $inv"
    }
    /*
     * Reorders the given `inv` in the list of transmitted inventories. This ensures that updates
     * for inventories are sent in the order they were added when this function was called, even if
     * they were first added during login (e.g., `worn` and `inv`).
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
