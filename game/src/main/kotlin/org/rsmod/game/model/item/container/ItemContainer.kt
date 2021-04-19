package org.rsmod.game.model.item.container

import org.rsmod.game.model.item.Item

class ItemContainer private constructor(
    private val items: MutableList<Item?>,
    val stackMode: ItemContainerStackMode,
    var autoUpdate: Boolean = false
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
        return if (stackMode.stacks(item.type)) {
            first { it?.id == item.id }?.amount ?: 0
        } else {
            foldRight(0) { it, amount ->
                if (it?.id == item.id) amount + it.amount else amount
            }
        }
    }

    fun clear() {
        items.indices.forEach {
            items[it] = null
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
    }

    /**
     * Creates a deep-copy of this [ItemContainer].
     */
    fun copy(): ItemContainer {
        val copyItems = mutableListOf<Item?>()
        items.forEach { copyItems.add(it?.copy()) }
        return ItemContainer(copyItems, stackMode, autoUpdate)
    }
}
