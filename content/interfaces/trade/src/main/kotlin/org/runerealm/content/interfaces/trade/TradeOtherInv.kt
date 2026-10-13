package org.runerealm.content.interfaces.trade

import net.rsprot.protocol.common.game.outgoing.inv.InventoryObject
import net.rsprot.protocol.game.outgoing.inv.UpdateInvFull
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.InvObj
import org.runerealm.game.inv.Inventory

/**
 * The client treats an inv update whose combined interface id is below `-70000` as an update for
 * the "other" copy of the inv (`invother_getobj`/`invother_getnum` in cs2, inv id + 32768). The
 * trade screens draw the partner's offer from `invother tradeoffer`.
 */
internal const val OTHER_INV_COMBINED_ID: Int = -70_000 - 1234

/** Sends [offer] (the trade partner's offer) as this player's "other" `tradeoffer` inv. */
internal fun Player.updateOtherInv(offer: Inventory) {
    val provider = OtherObjProvider(offer.objs)
    val message =
        UpdateInvFull(
            OTHER_INV_COMBINED_ID - offer.type.id,
            offer.type.id,
            offer.lastOccupiedSlot(),
            provider,
        )
    client.write(message)
}

private class OtherObjProvider(private val objs: Array<InvObj?>) : UpdateInvFull.ObjectProvider {
    override fun provide(slot: Int): Long {
        val obj = objs.getOrNull(slot) ?: return InventoryObject.NULL
        return InventoryObject(slot, obj.id, obj.count)
    }
}
