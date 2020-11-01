package org.rsmod.game.model.item

import org.rsmod.game.model.item.type.ItemType

class Item(
    val type: ItemType,
    val amount: Int
) {

    val id: Int
        get() = type.id
}
