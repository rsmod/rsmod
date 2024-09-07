package org.rsmod.api.inv.weight

import jakarta.inject.Inject
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.obj.ObjTypeList

public class InvWeight @Inject constructor(objTypes: ObjTypeList) {
    private val weights = objTypes.values.associate { it.id to it.weight }

    public fun sumOf(invs: Iterable<Inventory>): Int =
        invs.sumOf { if (it.type.runWeight) sum(it) else 0 }

    public fun sum(inv: Inventory): Int = inv.objs.sumOf { if (it != null) get(it.id) else 0 }

    private operator fun get(obj: Int): Int = weights[obj] ?: 0
}
