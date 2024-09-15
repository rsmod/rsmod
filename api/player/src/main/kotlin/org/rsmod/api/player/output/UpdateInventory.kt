package org.rsmod.api.player.output

import net.rsprot.protocol.common.game.outgoing.inv.InventoryObject
import net.rsprot.protocol.game.outgoing.inv.UpdateInvFull
import net.rsprot.protocol.game.outgoing.inv.UpdateInvPartial
import net.rsprot.protocol.game.outgoing.inv.UpdateInvStopTransmit
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj

/** @see [UpdateInvFull] */
public fun Player.updateInvFull(inv: Inventory) {
    val highestSlot = inv.indexOfLast { it != null } + 1
    val provider = RspObjProvider(inv.objs)
    val message = UpdateInvFull(inv.type.id, highestSlot, provider)
    client.write(message)
}

public object UpdateInventory {
    /** @see [UpdateInvPartial] */
    public fun updateInvPartial(player: Player, inv: Inventory, vararg updateSlots: Int) {
        val provider = RspIndexedObjProvider(inv.objs, updateSlots.iterator())
        val message = UpdateInvPartial(inv.type.id, provider)
        player.client.write(message)
    }

    /** @see [UpdateInvStopTransmit] */
    public fun updateInvStopTransmit(player: Player, inv: Inventory) {
        player.client.write(UpdateInvStopTransmit(inv.type.id))
    }
}

private class RspObjProvider(private val objs: Array<InvObj?>) : UpdateInvFull.ObjectProvider {
    override fun provide(slot: Int): InventoryObject {
        val obj = objs.getOrNull(slot) ?: return InventoryObject.NULL
        return InventoryObject(slot, obj.id, obj.count)
    }
}

private class RspIndexedObjProvider(private val objs: Array<InvObj?>, updateSlots: Iterator<Int>) :
    UpdateInvPartial.IndexedObjectProvider(updateSlots) {
    override fun provide(slot: Int): InventoryObject {
        val obj = objs.getOrNull(slot) ?: return InventoryObject(slot, -1, -1)
        return InventoryObject(slot, obj.id, obj.count)
    }
}
