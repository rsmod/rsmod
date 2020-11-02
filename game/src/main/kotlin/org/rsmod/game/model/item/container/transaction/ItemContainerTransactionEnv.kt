package org.rsmod.game.model.item.container.transaction

import kotlin.math.min
import org.rsmod.game.model.item.Item
import org.rsmod.game.model.item.MAX_ITEM_STACK
import org.rsmod.game.model.item.container.ItemContainer
import org.rsmod.game.model.item.container.ItemContainerStackMode
import org.rsmod.game.model.item.type.ItemType

fun transaction(
    container: ItemContainer,
    block: ItemContainerTransactionEnv.() -> Unit
): Boolean {
    val transaction = ItemContainerTransaction(container, autoCommit = false)
    val environment = ItemContainerTransactionEnv(transaction)
    block(environment)
    return transaction.commit()
}

data class ItemContainerTransactionResult(
    val requested: Int,
    val completed: Int
) {

    val left: Int
        get() = completed - requested

    val success: Boolean
        get() = left == 0

    val partial: Boolean
        get() = completed > 0

    val failure: Boolean
        get() = !success
}

class ItemContainerTransactionEnv(
    private val transaction: ItemContainerTransaction,
    private var strict: Boolean = true
) {

    fun strict() {
        strict = true
    }

    fun lenient() {
        strict = false
    }

    fun add(item: Item, slot: Int = 0) {
        val stack = stacks(item.type, transaction.stackMode)
        val strict = this.strict
        transaction.query {
            val add = if (stack) {
                addStack(item, slot)
            } else {
                addScatter(item, slot)
            }
            add.success || !strict && add.partial
        }
    }

    fun remove(id: Int, amount: Int = 1, slot: Int = 0) {
        val strict = this.strict
        transaction.query {
            val result = remove(id, amount, slot)
            result.success || !strict && result.partial
        }
    }
}

internal fun MutableList<Item?>.remove(
    id: Int,
    amount: Int,
    slot: Int = 0
): ItemContainerTransactionResult {
    var removed = 0
    for (i in 0 until amount) {
        val index = indexOf(id, slot) ?: break
        val item = get(index) ?: break
        val remove = amount.coerceAtMost(item.amount)
        val left = item.amount - remove

        this[index] = if (left == 0) null else Item(item.type, left)
        removed += remove
        if (removed >= amount) {
            break
        }
    }
    return result(amount, removed)
}

internal fun MutableList<Item?>.addStack(item: Item, slot: Int): ItemContainerTransactionResult {
    val index = indexOf(item.id, slot) ?: indexOfNull() ?: return result(item.amount, 0)
    val oldAmount = (get(index)?.amount ?: 0)
    val cap = MAX_ITEM_STACK - oldAmount
    if (cap <= 0) {
        return result(item.amount, 0)
    }
    val amount = min(item.amount, cap)
    this[index] = Item(item.type, amount + oldAmount)
    return result(item.amount, amount)
}

internal fun MutableList<Item?>.addScatter(item: Item, slot: Int): ItemContainerTransactionResult {
    for (i in 0 until item.amount) {
        val index = indexOfNull(if (slot == 0) 0 else (slot + i)) ?: return result(item.amount, i)
        this[index] = Item(item.type, amount = 1)
    }
    return result(item.amount, item.amount)
}

internal fun List<Item?>.indexOf(id: Int, startIndex: Int = 0): Int? {
    /* try to find item slot starting from given index */
    for (i in startIndex until size) {
        val item = get(i) ?: continue
        if (item.id == id) {
            return i
        }
    }
    /* if item from slot is not found, find it from previous spaces */
    for (i in 0 until startIndex) {
        val item = get(i) ?: continue
        if (item.id == id) {
            return i
        }
    }
    return null
}

internal fun MutableList<Item?>.indexOfNull(startIndex: Int = 0): Int? {
    /* try to find empty slot starting from given index */
    for (i in startIndex until size) {
        if (this[i] != null) continue
        return i
    }
    /* if item from slot is not found, find it from previous spaces */
    for (i in 0 until startIndex) {
        if (this[i] != null) continue
        return i
    }
    return null
}

internal fun stacks(type: ItemType, mode: ItemContainerStackMode): Boolean = when (mode) {
    ItemContainerStackMode.Never -> false
    ItemContainerStackMode.Always -> true
    ItemContainerStackMode.Default -> type.stacks
}

private fun result(requested: Int, completed: Int): ItemContainerTransactionResult {
    return ItemContainerTransactionResult(requested, completed)
}
