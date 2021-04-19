package org.rsmod.game.model.item.container

import org.rsmod.game.model.ui.Component

open class ItemContainerKey(
    val name: String,
    val capacity: Int,
    val stack: ItemContainerStackMode,
    val clientId: Int? = null,
    val component: Component? = null
) {

    companion object {

        fun of(
            name: String,
            stack: ItemContainerStackMode,
            capacity: Int,
            clientId: Int,
            component: Component
        ): ItemContainerKey {
            return ItemContainerKey(name, capacity, stack, clientId, component)
        }

        fun ofClientId(
            name: String,
            stack: ItemContainerStackMode,
            capacity: Int,
            clientId: Int
        ): ItemContainerKey {
            return ItemContainerKey(name, capacity, stack, clientId, null)
        }

        fun ofComponent(
            name: String,
            stack: ItemContainerStackMode,
            capacity: Int,
            component: Component
        ): ItemContainerKey {
            return ItemContainerKey(name, capacity, stack, null, component)
        }
    }
}
