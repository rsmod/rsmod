package org.rsmod.plugins.api.event

import org.rsmod.game.event.Event
import org.rsmod.game.model.item.type.ItemType
import org.rsmod.game.model.ui.Component

data class ItemSearchInput(val item: ItemType) : Event
data class ContinueDialogue(val component: Component, val slot: Int) : Event
