package org.rsmod.plugins.api.model.item

import org.rsmod.game.model.item.Item
import org.rsmod.game.model.item.type.ItemTypeList
import org.rsmod.plugins.api.cache.type.item.isNoted
import org.rsmod.plugins.api.cache.type.item.canBeNoted

val Item.isNoted: Boolean
    get() = type.isNoted

val Item.canBeNoted: Boolean
    get() = type.canBeNoted

fun Item.definiteName(types: ItemTypeList): String {
    if (isNoted) {
        val notedId = type.noteLink
        return types.getOrNull(notedId)?.name ?: name
    }
    return name
}
