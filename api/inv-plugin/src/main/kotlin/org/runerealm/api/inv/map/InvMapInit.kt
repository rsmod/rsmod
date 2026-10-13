package org.runerealm.api.inv.map

import jakarta.inject.Inject
import org.runerealm.api.config.refs.BaseInvs
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.Inventory
import org.runerealm.game.type.inv.InvTypeList
import org.runerealm.game.type.inv.UnpackedInvType

public class InvMapInit @Inject constructor(invs: InvTypeList) {
    public val defaultInvs: MutableSet<UnpackedInvType> =
        hashSetOf(invs[BaseInvs.inv], invs[BaseInvs.worn])

    public fun init(player: Player) {
        putIfAbsent(player)
        cacheCommons(player)
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

    public operator fun plusAssign(inv: UnpackedInvType) {
        defaultInvs += inv
    }
}
