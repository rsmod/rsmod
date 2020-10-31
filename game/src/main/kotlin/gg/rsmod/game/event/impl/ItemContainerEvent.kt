package gg.rsmod.game.event.impl

import gg.rsmod.game.event.Event
import gg.rsmod.game.model.item.ItemContainerKey
import gg.rsmod.game.model.item.ItemContainer
import gg.rsmod.game.model.mob.Player

data class ItemContainerUpdate(
    val player: Player,
    val key: ItemContainerKey,
    val container: ItemContainer
) : Event
