package org.rsmod.objtx

public data class TransactionResultList<T>(
    public val output: (TransactionObj?) -> T?,
    public val inventories: List<TransactionInventory<T>>,
    public val results: List<TransactionResult>,
    public val err: TransactionResult.Err? = results.findErr(),
) {
    public var commited: Boolean = false
        private set

    public val size: Int
        get() = results.size

    public val success: Boolean
        get() = err == null

    public val failure: Boolean
        get() = !success

    public fun commitAll() {
        check(!commited) { "Transaction already commited!" }
        inventories.commitAll()
        commited = true
    }

    public fun single(): TransactionResult = results.single()

    public fun completed(): Int = results.sumOf { if (it.isOk()) it.completed else 0 }

    public fun anyCompleted(): Boolean = results.any { it.isOk() && it.completed > 0 }

    public inline fun <reified T : TransactionResult> resolve(index: Int): T? {
        val result = results.getOrNull(index) ?: return null
        return result as? T
    }

    /**
     * Gets the [TransactionResult] in index [index] within the ordered [results] collection _only
     * if_ the respective [TransactionResult] is an instance of [TransactionResult.Ok].
     *
     * [TransactionResult]s of all applicable types can be retrieved via [results] list or the
     * [resolve] function that takes in a type parameter.
     *
     * @return `null` if the [index] is out of bounds, or if the retrieved entry is _not_ a
     *   [TransactionResult.Ok].
     */
    public operator fun get(index: Int): TransactionResult.Ok? {
        val result = results.getOrNull(index) ?: return null
        return result as? TransactionResult.Ok
    }

    private fun List<TransactionInventory<T>>.commitAll() = forEach { it.commit() }

    private fun TransactionInventory<T>.commit() {
        for (i in output.indices) {
            val converted = output(this[i])
            output[i] = converted
        }
    }

    private companion object {
        fun List<TransactionResult>.findErr(): TransactionResult.Err? {
            return filterIsInstance<TransactionResult.Err>().firstOrNull()
        }
    }
}
