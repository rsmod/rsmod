package org.rsmod.plugins.api.event

import org.rsmod.game.event.Event
import org.rsmod.game.model.item.type.ItemType
import org.rsmod.game.model.mob.Player

data class EquipItem(
    val player: Player,
    val item: ItemType,
    val slot: Int
) : Event
