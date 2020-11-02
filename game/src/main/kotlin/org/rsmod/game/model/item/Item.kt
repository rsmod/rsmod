package org.rsmod.game.model.item

import org.rsmod.game.model.item.type.ItemType

internal const val MAX_ITEM_STACK = Int.MAX_VALUE

class Item(
    val type: ItemType,
    val amount: Int = 1
) {

    val id: Int
        get() = type.id
}
