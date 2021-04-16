package org.rsmod.plugins.api.event

import org.rsmod.game.event.Event
import org.rsmod.game.model.item.type.ItemType

data class ItemSearchInput(val item: ItemType) : Event
