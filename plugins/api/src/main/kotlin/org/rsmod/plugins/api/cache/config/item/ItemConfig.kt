package org.rsmod.plugins.api.cache.config.item

import org.rsmod.game.model.item.type.ItemTypeBuilder

data class ItemConfig(
    val id: Int,
    val inherit: Int?,
    val dataFile: String?,
    val pack: Boolean,
    val builder: ItemTypeBuilder
)
