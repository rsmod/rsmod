package org.rsmod.plugins.api.model.item

import org.rsmod.game.model.item.Item
import org.rsmod.game.model.item.type.ItemTypeList

val Item.noted: Boolean
    get() = type.noteValue > 0

val Item.unnoted: Boolean
    get() = type.noteValue == 0 && type.noteLink > 0

fun Item.definiteName(types: ItemTypeList): String {
    if (noted) {
        val notedId = type.noteLink
        return types.getOrNull(notedId)?.name ?: name
    }
    return name
}
