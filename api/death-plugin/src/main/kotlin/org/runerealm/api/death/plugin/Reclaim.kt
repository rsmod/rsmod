package org.runerealm.api.death.plugin

import org.runerealm.api.config.refs.objs
import org.runerealm.api.death.Gravestones
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.utils.format.formatAmount
import org.runerealm.game.inv.InvObj
import org.runerealm.game.inv.Inventory
import org.runerealm.game.type.obj.ObjTypeList

/**
 * Moves as many items from [from] (a grave or Death's Office) into the player's inventory as fit,
 * after paying the fee [feeFor] charges for them. The fee is taken from the bank, or else from the
 * inventory (there is no Death's coffer yet).
 *
 * Returns `null` if nothing was collected (no items, no space, or not enough coins), otherwise
 * `true`.
 */
internal fun ProtectedAccess.reclaim(
    gravestones: Gravestones,
    objTypes: ObjTypeList,
    from: Inventory,
    feeFor: (List<InvObj>) -> Int,
): Boolean? {
    if (from.isEmpty()) {
        return null
    }
    val slots = fittingSlots(objTypes, from)
    if (slots.isEmpty()) {
        mes("You don't have enough inventory space to collect your items.")
        return null
    }
    val fee = feeFor(slots.map { checkNotNull(from[it]) })
    if (!payFee(fee)) {
        mes("You need ${fee.formatAmount} coins in your bank or inventory to collect your items.")
        return null
    }
    if (fee > 0) {
        mes("You pay a fee of ${fee.formatAmount} coins to collect your items.")
    }
    for (slot in slots) {
        val obj = from[slot] ?: continue
        from[slot] = gravestones.deposit(inv, obj)
    }
    return true
}

private fun ProtectedAccess.payFee(fee: Int): Boolean {
    if (fee <= 0) {
        return true
    }
    if (invTotal(bank, objs.coins) >= fee) {
        return invTakeFee(fee, bank)
    }
    if (invCoinTotal() >= fee) {
        return invTakeFee(fee)
    }
    return false
}

/** The slots of [from] whose items fit in the player's inventory. */
private fun ProtectedAccess.fittingSlots(objTypes: ObjTypeList, from: Inventory): List<Int> {
    var free = inv.freeSpace()
    val stacks = inv.filterNotNull().map { it.id }.toMutableSet()
    val slots = mutableListOf<Int>()
    for (slot in from.indices) {
        val obj = from[slot] ?: continue
        val stackable = objTypes[obj].isStackable
        if (stackable && obj.id in stacks) {
            slots += slot
            continue
        }
        if (free == 0) {
            continue
        }
        free--
        slots += slot
        if (stackable) {
            stacks += obj.id
        }
    }
    return slots
}
