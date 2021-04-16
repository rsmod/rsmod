package org.rsmod.plugins.api.cache.type.item

import org.rsmod.game.model.item.type.ItemType

private val EQUIPMENT_OPTION_PARAMS = intArrayOf(
    451,
    452,
    453,
    454,
    455,
    456,
    457,
    458
)

val ItemType.isNoted: Boolean
    get() = noteValue > 0

val ItemType.canBeNoted: Boolean
    get() = noteValue == 0 && noteLink > 0

fun ItemType.equipmentOptions(): List<String> {
    val options = mutableListOf<String>()
    EQUIPMENT_OPTION_PARAMS.forEach { key ->
        val opt = strParameters[key] ?: return@forEach
        options.add(opt)
    }
    return options
}
