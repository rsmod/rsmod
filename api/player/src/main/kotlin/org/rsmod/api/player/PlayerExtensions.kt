package org.rsmod.api.player

import org.rsmod.api.player.output.UpdateInventory
import org.rsmod.api.player.output.clearMapFlag
import org.rsmod.api.player.output.mes
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

public fun Player.startInvTransmit(inv: Inventory) {
    check(inv.type.scope == InvScope.Perm || !invMap.contains(inv.type)) {
        "`inv` should have previously been removed from cached inv map: $inv"
    }
    invMap[inv.type] = inv
    transmittedInvs.add(inv.type.id)
    UpdateInventory.updateInvFull(this, inv)
}

public fun Player.stopInvTransmit(inv: Inventory) {
    if (inv.type.scope != InvScope.Perm) {
        val removed = invMap.remove(inv.type)
        check(removed == inv) { "Mismatch with cached value: (cached=$removed, inv=$inv)" }
    }
    transmittedInvs.remove(inv.type.id)
    UpdateInventory.updateInvStopTransmit(this, inv)
}
