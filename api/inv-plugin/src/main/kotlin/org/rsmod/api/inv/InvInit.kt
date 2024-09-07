package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.config.refs.BaseInvs
import org.rsmod.api.player.updateInvFull
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.inv.InvTypeList
import org.rsmod.game.type.inv.UnpackedInvType

public class InvInit @Inject constructor(invs: InvTypeList) {
    public val defaultInvs: MutableSet<UnpackedInvType> =
        hashSetOf(invs[BaseInvs.inv], invs[BaseInvs.worn])

    public fun init(player: Player) {
        putIfAbsent(player)
        cacheCommons(player)
        sendInvs(player, player.invMap.values)
    }

    public fun putIfAbsent(player: Player) {
        for (default in defaultInvs) {
            if (default !in player.invMap) {
                val create = Inventory.create(default)
                player.invMap[default] = create
            }
        }
    }

    public fun cacheCommons(player: Player) {
        player.inv = player.invMap.getValue(BaseInvs.inv)
        player.worn = player.invMap.getValue(BaseInvs.worn)
    }

    public fun sendInvs(player: Player, invs: Iterable<Inventory>) {
        for (inv in invs) {
            player.updateInvFull(inv)
        }
    }

    public operator fun plusAssign(inv: UnpackedInvType) {
        defaultInvs += inv
    }
}
