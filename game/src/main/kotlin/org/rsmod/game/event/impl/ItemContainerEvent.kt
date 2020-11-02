package org.rsmod.game.event.impl

import org.rsmod.game.event.Event
import org.rsmod.game.model.item.container.ItemContainer
import org.rsmod.game.model.item.container.ItemContainerKey
import org.rsmod.game.model.mob.Player

data class ItemContainerInitialize(
    val player: Player,
    val key: ItemContainerKey,
    val container: ItemContainer
) : Event

data class ItemContainerUpdate(
    val player: Player,
    val key: ItemContainerKey,
    val container: ItemContainer
) : Event
