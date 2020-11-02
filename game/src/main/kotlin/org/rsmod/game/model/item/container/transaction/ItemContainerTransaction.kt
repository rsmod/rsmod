package org.rsmod.game.model.item.container.transaction

import java.util.LinkedList
import java.util.Queue
import org.rsmod.game.model.item.Item
import org.rsmod.game.model.item.container.ItemContainerStackMode
import org.rsmod.game.model.item.container.ItemContainer

private typealias ItemContainerQuery = (MutableList<Item?>).() -> Boolean

class ItemContainerTransaction(
    private val beforeImage: ItemContainer,
    private val items: MutableList<Item?> = beforeImage.toMutableList(),
    private val queries: Queue<ItemContainerQuery> = LinkedList(),
    private var autoCommit: Boolean = false
) {

    internal val stackMode: ItemContainerStackMode
        get() = beforeImage.stackMode

    fun commit(): Boolean {
        if (queries.isEmpty()) {
            /* nothing to commit */
            return true
        }
        val success = processQueries()
        if (success) {
            applyChanges()
            return true
        }
        return false
    }

    fun rollBack() {
        items.clear()
        items.addAll(beforeImage)
        queries.clear()
    }

    fun autoCommit(autoCommit: Boolean = true): ItemContainerTransaction {
        this.autoCommit = autoCommit
        return this
    }

    fun query(block: MutableList<Item?>.() -> Boolean): Boolean {
        queries.add(block)
        if (autoCommit) {
            return commit()
        }
        return true
    }

    private fun processQueries(): Boolean {
        return queries.all { query -> query(items) }
    }

    private fun applyChanges() {
        items.forEachIndexed { index, item ->
            beforeImage[index] = item
        }
    }
}
