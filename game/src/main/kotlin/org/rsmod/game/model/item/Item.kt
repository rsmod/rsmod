package org.rsmod.game.model.item

import com.google.common.base.MoreObjects
import org.rsmod.game.model.item.type.ItemType

internal const val MAX_ITEM_STACK = Int.MAX_VALUE

class Item(
    val type: ItemType,
    val amount: Int = 1
) {

    val id: Int
        get() = type.id

    val name: String
        get() = type.name

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("id", type.id)
        .add("amount", amount)
        .toString()
}
