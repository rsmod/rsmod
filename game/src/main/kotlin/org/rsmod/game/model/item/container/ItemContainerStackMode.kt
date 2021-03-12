package org.rsmod.game.model.item.container

import org.rsmod.game.model.item.type.ItemType

sealed class ItemContainerStackMode {
    object Default : ItemContainerStackMode()
    object Always : ItemContainerStackMode()
    object Never : ItemContainerStackMode()
}

internal fun ItemContainerStackMode.stacks(type: ItemType): Boolean = when (this) {
    ItemContainerStackMode.Never -> false
    ItemContainerStackMode.Always -> true
    ItemContainerStackMode.Default -> type.stacks
}
