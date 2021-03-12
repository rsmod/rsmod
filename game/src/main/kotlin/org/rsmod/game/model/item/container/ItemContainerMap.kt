package org.rsmod.game.model.item.container

class ItemContainerMap(
    private val containers: MutableMap<ItemContainerKey, ItemContainer> = mutableMapOf()
) : Map<ItemContainerKey, ItemContainer> by containers {

    fun register(
        key: ItemContainerKey,
        capacity: Int,
        stack: ItemContainerStackMode = ItemContainerStackMode.Default
    ) {
        if (containsKey(key)) {
            error("Container key already registered (key=$key)")
        }
        containers[key] = ItemContainer(capacity, stack)
    }

    operator fun set(key: ItemContainerKey, container: ItemContainer) {
        containers[key] = container
    }

    /**
     * Creates a deep-copy of this [ItemContainerMap] with [ItemContainer]s
     * that have [ItemContainer.autoUpdate] set as true.
     */
    fun copyAutoUpdateOnly(): ItemContainerMap {
        val map = mutableMapOf<ItemContainerKey, ItemContainer>()
        containers.forEach { (key, container) ->
            if (!container.autoUpdate) {
                /* we only care to copy containers that auto-update */
                return@forEach
            }
            map[key] = container.copy()
        }
        return ItemContainerMap(map)
    }
}
