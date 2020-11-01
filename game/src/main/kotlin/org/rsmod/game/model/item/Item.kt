package org.rsmod.game.model.item

class Item(
    val type: ItemType,
    val amount: Int
) {

    val id: Int
        get() = type.id
}
