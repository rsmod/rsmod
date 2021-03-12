package org.rsmod.game.model.item.container

import org.rsmod.game.model.ui.Component

open class ItemContainerKey private constructor(
    val id: Int? = null,
    val component: Component? = null
) {

    companion object {

        fun of(id: Int, component: Component): ItemContainerKey {
            return ItemContainerKey(id, component)
        }

        fun ofId(id: Int): ItemContainerKey {
            return ItemContainerKey(id, null)
        }

        fun ofComponent(component: Component): ItemContainerKey {
            return ItemContainerKey(null, component)
        }
    }
}
