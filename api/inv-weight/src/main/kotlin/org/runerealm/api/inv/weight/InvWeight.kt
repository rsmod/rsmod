package org.runerealm.api.inv.weight

import kotlin.collections.iterator
import org.runerealm.game.entity.Player
import org.runerealm.game.type.obj.ObjTypeList

public object InvWeight {
    public fun calculateWeightInGrams(player: Player, objTypes: ObjTypeList): Int {
        var grams = 0
        for (transmitted in player.transmittedInvs.intIterator()) {
            val inv = player.invMap.backing[transmitted]
            checkNotNull(inv) { "Inv expected in `invMap`: $transmitted (invMap=${player.invMap})" }

            val affectsWeight = inv.type.runWeight
            if (!affectsWeight) {
                continue
            }

            for (i in inv.indices) {
                val obj = inv[i] ?: continue
                grams += objTypes[obj].weight
            }
        }
        return grams
    }
}
