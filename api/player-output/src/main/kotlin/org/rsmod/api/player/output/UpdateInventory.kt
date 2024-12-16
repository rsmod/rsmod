package org.rsmod.api.player.output

import java.util.BitSet
import net.rsprot.protocol.common.game.outgoing.inv.InventoryObject
import net.rsprot.protocol.game.outgoing.inv.UpdateInvFull
import net.rsprot.protocol.game.outgoing.inv.UpdateInvPartial
import net.rsprot.protocol.game.outgoing.inv.UpdateInvStopTransmit
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj

/** @see [UpdateInvFull] */
public fun Player.updateInvFull(inv: Inventory) {
    val highestSlot = inv.lastOccupiedSlot()
    val provider = RspObjProvider(inv.objs)
    val message = UpdateInvFull(-(1234 + inv.type.id), inv.type.id, highestSlot, provider)
    client.write(message)
}

/**
 * Updates the inventory with either a full update ([UpdateInvFull]) or a partial update
 * ([UpdateInvPartial]), depending on how many slots in [inv] have been modified since the last
 * update or since [Inventory.modifiedSlots] was last reset.
 *
 * If a full update is recommended (based on [isFullUpdateRecommended]), all inventory slots are
 * sent. Otherwise, only the modified slots are sent as a partial update.
 *
 * **Note**: This function _does not_ reset or alter [Inventory.modifiedSlots].
 */
public fun Player.updateInvRecommended(inv: Inventory) {
    if (inv.isFullUpdateRecommended()) {
        updateInvFull(inv)
    } else {
        val changedSlots = inv.modifiedSlots.asSequence().iterator()
        UpdateInventory.updateInvPartial(this, inv, changedSlots)
    }
}

public object UpdateInventory {
    /** @see [UpdateInvPartial] */
    public fun updateInvPartial(player: Player, inv: Inventory, updateSlots: Iterator<Int>) {
        val provider = RspIndexedObjProvider(inv.objs, updateSlots)
        val message = UpdateInvPartial(-1, -(1234 + inv.type.id), inv.type.id, provider)
        player.client.write(message)
    }

    /** @see [UpdateInvStopTransmit] */
    public fun updateInvStopTransmit(player: Player, inv: Inventory) {
        player.client.write(UpdateInvStopTransmit(inv.type.id))
    }
}

/**
 * Returns `true` if the [Inventory] in scope should receive a "full" update based on the
 * recommendation within rsprot.
 */
private fun Inventory.isFullUpdateRecommended(): Boolean {
    val modifiedSlotCount = modifiedSlots.cardinality()
    val highestModifiedIndex = modifiedSlots.length() - 1
    val percentageModified =
        if (highestModifiedIndex >= 0) {
            modifiedSlotCount.toDouble() / (highestModifiedIndex + 1)
        } else {
            0.0
        }
    return percentageModified >= 0.66
}

private fun BitSet.asSequence(): Sequence<Int> = sequence {
    var index = nextSetBit(0)
    while (index >= 0) {
        yield(index)
        index = nextSetBit(index + 1)
    }
}

private class RspObjProvider(private val objs: Array<InvObj?>) : UpdateInvFull.ObjectProvider {
    override fun provide(slot: Int): Long {
        val obj = objs.getOrNull(slot) ?: return InventoryObject.NULL
        return InventoryObject(slot, obj.id, obj.count)
    }
}

private class RspIndexedObjProvider(private val objs: Array<InvObj?>, updateSlots: Iterator<Int>) :
    UpdateInvPartial.IndexedObjectProvider(updateSlots) {
    override fun provide(slot: Int): Long {
        val obj = objs.getOrNull(slot) ?: return InventoryObject(slot, -1, -1)
        return InventoryObject(slot, obj.id, obj.count)
    }
}
