package org.rsmod.game.model.item.container

import org.rsmod.game.model.item.Item

open class ItemContainerKey

sealed class ContainerStack {
    object Default : ContainerStack()
    object Always : ContainerStack()
    object Never : ContainerStack()
}

class ItemContainer private constructor(
    private val items: MutableList<Item?>,
    val stack: ContainerStack,
    var update: Boolean = false
) : List<Item?> by items {

    constructor(
        capacity: Int = 0,
        stack: ContainerStack = ContainerStack.Default
    ) : this(
        arrayOfNulls<Item?>(capacity).toMutableList(),
        stack
    )

    fun grow(capacity: Int) {
        if (capacity <= items.size) return
        val amount = capacity - items.size
        items.addAll(arrayOfNulls(amount))
    }

    operator fun set(slot: Int, item: Item?) {
        items[slot] = item
        update = true
    }
}

class ItemContainerMap(
    private val containers: MutableMap<ItemContainerKey, ItemContainer> = mutableMapOf()
) : Map<ItemContainerKey, ItemContainer> by containers {

    fun register(
        key: ItemContainerKey,
        capacity: Int,
        stack: ContainerStack = ContainerStack.Default
    ) {
        if (containsKey(key)) {
            error("Container key already registered (key=$key)")
        }
        containers[key] = ItemContainer(capacity, stack)
    }

    operator fun set(key: ItemContainerKey, container: ItemContainer) {
        containers[key] = container
    }
}
