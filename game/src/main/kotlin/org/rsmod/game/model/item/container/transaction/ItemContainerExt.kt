package org.rsmod.game.model.item.container.transaction

import org.rsmod.game.model.item.Item
import org.rsmod.game.model.item.container.ItemContainer
import org.rsmod.game.model.item.container.stacks

operator fun ItemContainer.plusAssign(item: Item) {
    add(item)
}

operator fun ItemContainer.minusAssign(item: Item) {
    remove(item)
}

fun ItemContainer.transaction(
    block: ItemContainerTransactionEnv.() -> Unit
): Boolean {
    val transaction = ItemContainerTransaction(this, autoCommit = false)
    val environment = ItemContainerTransactionEnv(transaction, stackMode)
    block(environment)
    return transaction.commit()
}

fun ItemContainer.add(item: Item, slot: Int = 0, strict: Boolean = true): ItemContainerTransactionResult {
    val transaction = ItemContainerTransaction(this)
    var queryResult: ItemContainerTransactionResult? = null

    val stacks = stackMode.stacks(item.type)
    transaction.query {
        val add = if (stacks) {
            addStack(item, slot)
        } else {
            addScatter(item, slot)
        }
        /* cache transaction result from the single-query block */
        queryResult = add
        add.success || !strict && add.partial
    }

    /* make sure single-query was successful */
    val success = transaction.commit()
    val result = queryResult
    if (!success || result == null) {
        /* though transaction is discarded, roll back all queries */
        transaction.rollBack()
        return emptyResult(item.amount)
    }

    return result
}

fun ItemContainer.remove(
    item: Item,
    slot: Int = 0,
    strict: Boolean = true
): ItemContainerTransactionResult {
    val transaction = ItemContainerTransaction(this)
    var queryResult: ItemContainerTransactionResult? = null

    transaction.query {
        val remove = remove(item.id, item.amount, slot)
        /* cache transaction result from the single-query block */
        queryResult = remove
        remove.success || !strict && remove.partial
    }

    /* make sure single-query was successful */
    val success = transaction.commit()
    val result = queryResult
    if (!success || result == null) {
        /* though transaction is discarded, roll back all queries */
        transaction.rollBack()
        return emptyResult(item.amount)
    }
    return result
}

private fun emptyResult(requested: Int): ItemContainerTransactionResult {
    return ItemContainerTransactionResult(requested, completed = 0)
}
