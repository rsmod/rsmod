package org.rsmod.plugins.profile.dispatch

import java.util.concurrent.atomic.AtomicBoolean

private typealias TransactionList<L, R> = ArrayDeque<DispatchTransaction<L, R>>

public abstract class TransactionDispatch<L: DispatchRequest, R : DispatchResponse> {

    private val requests = TransactionList<L, R>()
    private val pending = TransactionList<L, R>()
    private val busy = AtomicBoolean()

    internal fun serve(transactionLimit: Int) {
        val requests = requests.take(pending)
        if (requests.isEmpty()) return
        busy.set(true)
        for (i in 0 until transactionLimit) {
            val transaction = requests.removeFirstOrNull() ?: break
            val response = serve(transaction.request)
            transaction.response = response
            transaction.ready.set(true)
        }
        busy.set(false)
    }

    public fun query(request: L): DispatchTransaction<L, R> {
        val transaction = DispatchTransaction<L, R>(request = request)
        pushTransaction(transaction)
        return transaction
    }

    private fun pushTransaction(transaction: DispatchTransaction<L, R>) {
        if (busy.get()) {
            pending += transaction
        } else {
            requests += transaction
        }
    }

    internal abstract fun serve(request: L): R

    private companion object {

        private fun <L : DispatchRequest, R : DispatchResponse> TransactionList<L, R>.take(
            other: TransactionList<L, R>
        ): TransactionList<L, R> {
            if (other.isEmpty()) return this
            this += other
            other.clear()
            return this
        }
    }
}
