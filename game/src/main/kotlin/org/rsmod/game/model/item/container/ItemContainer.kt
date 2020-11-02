package org.rsmod.game.model.item.container

import org.rsmod.game.model.item.Item
import org.rsmod.game.model.item.container.transaction.stacks

open class ItemContainerKey

sealed class ItemContainerStackMode {
    object Default : ItemContainerStackMode()
    object Always : ItemContainerStackMode()
    object Never : ItemContainerStackMode()
}

class ItemContainer private constructor(
    private val items: MutableList<Item?>,
    val stackMode: ItemContainerStackMode,
    var initialized: Boolean = false,
    var update: Boolean = false
) : List<Item?> by items {

    constructor(
        capacity: Int = 0,
        stack: ItemContainerStackMode = ItemContainerStackMode.Default
    ) : this(
        arrayOfNulls<Item?>(capacity).toMutableList(),
        stack
    )

    fun ensureCapacity(capacity: Int) {
        if (capacity <= items.size) return
        val amount = capacity - items.size
        items.addAll(arrayOfNulls(amount))
    }

    fun amount(item: Item): Int {
        return if (stacks(item.type, stackMode)) {
            first { it?.id == item.id }?.amount ?: 0
        } else {
            foldRight(0) { it, amount ->
                if (it?.id == item.id) amount + it.amount else amount
            }
        }
    }

    fun isFull(): Boolean {
        return items.none { it == null }
    }

    fun free(): Int {
        return items.count { it == null }
    }

    fun occupied(): Int {
        return items.count { it != null }
    }

    override fun isEmpty(): Boolean {
        return items.all { it == null }
    }

    operator fun set(slot: Int, item: Item?) {
        check(item == null || item.amount > 0) { "If you want to remove item set it to `null` instead." }
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
}
