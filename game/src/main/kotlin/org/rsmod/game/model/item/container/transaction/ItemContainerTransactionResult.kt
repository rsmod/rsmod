package org.rsmod.game.model.item.container.transaction

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