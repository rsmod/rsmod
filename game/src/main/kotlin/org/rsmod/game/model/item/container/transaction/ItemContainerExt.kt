package org.rsmod.game.model.item.container.transaction

import org.rsmod.game.model.item.Item
import org.rsmod.game.model.item.container.ItemContainer

operator fun ItemContainer.plusAssign(item: Item) {
    add(item)
}

fun ItemContainer.add(item: Item, slot: Int = 0, strict: Boolean = true): ItemContainerTransactionResult {
    val transaction = ItemContainerTransaction(this)
    var queryResult: ItemContainerTransactionResult? = null

    val stacks = stacks(item.type, stackMode)
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
    id: Int,
    amount: Int = 1,
    slot: Int = 0,
    strict: Boolean = true
): ItemContainerTransactionResult {
    val transaction = ItemContainerTransaction(this)
    var queryResult: ItemContainerTransactionResult? = null

    transaction.query {
        val remove = remove(id, amount, slot)
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
        return emptyResult(amount)
    }
    return result
}

private fun emptyResult(requested: Int): ItemContainerTransactionResult {
    return ItemContainerTransactionResult(requested, completed = 0)
}
