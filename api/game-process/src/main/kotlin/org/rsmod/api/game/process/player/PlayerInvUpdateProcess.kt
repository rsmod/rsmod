package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.player.output.updateInvRecommended
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.inv.InvScope

public class PlayerInvUpdateProcess @Inject constructor(private val players: PlayerList) {
    private val sharedUpdatedInvs = hashSetOf<Inventory>()

    public fun process() {
        players.process()
        resetUpdatedInvs()
    }

    private fun PlayerList.process() {
        for (player in this) {
            val modalInv = player.modalInv
            if (modalInv != null) {
                player.updateInv(modalInv)
            }

            val modalSideInv = player.modalSideInv
            if (modalSideInv != null) {
                player.updateInv(modalSideInv)
            }
        }
    }

    private fun Player.updateInv(inv: Inventory) {
        if (!inv.hasModifiedSlots()) {
            return
        }
        updateInvRecommended(inv)
        if (inv.type.scope == InvScope.Shared) {
            sharedUpdatedInvs += inv
        } else {
            inv.clearModifiedSlots()
        }
    }

    private fun resetUpdatedInvs() {
        sharedUpdatedInvs.forEach(Inventory::clearModifiedSlots)
        sharedUpdatedInvs.clear()
    }
}
