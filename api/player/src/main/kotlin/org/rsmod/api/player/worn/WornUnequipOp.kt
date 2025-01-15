package org.rsmod.api.player.worn

import jakarta.inject.Inject
import org.rsmod.api.invtx.invTransfer
import org.rsmod.api.player.events.interact.InvEquipEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.objtx.TransactionResult

public class WornUnequipOp
@Inject
constructor(private val objTypes: ObjTypeList, private val eventBus: EventBus) {
    public fun unequip(
        player: Player,
        wornSlot: Int,
        worn: Inventory,
        into: Inventory,
    ): WornUnequipResult {
        val obj = worn[wornSlot] ?: return WornUnequipResult.Fail.InvalidObj
        val wearpos = Wearpos[wornSlot] ?: error("Wearpos `$wornSlot` not found.")

        val transaction =
            player.invTransfer(from = worn, fromSlot = wornSlot, into = into, count = obj.count)

        if (transaction.failure) {
            check(transaction.err is TransactionResult.NotEnoughSpace) {
                "Transaction error is expected to only be of " +
                    "`NotEnoughSpace` type: found=${transaction.err}"
            }
            val message = "You don't have enough free space to do that."
            return WornUnequipResult.Fail.NotEnoughInvSpace(message)
        }

        val unequip = InvEquipEvents.Unequip(player, wearpos, obj, objTypes[obj])
        eventBus.publish(unequip)

        return WornUnequipResult.Success
    }
}
