package org.rsmod.plugins.profile.dispatch.transaction

import org.rsmod.plugins.profile.dispatch.DispatchRequest
import org.rsmod.plugins.profile.dispatch.DispatchResponse
import java.util.concurrent.atomic.AtomicBoolean

public abstract class TransactionDispatch<L : DispatchRequest, R : DispatchResponse> {

    private val requests = ArrayDeque<DispatchTransaction<L, R>>()
    private val pending = ArrayDeque<DispatchTransaction<L, R>>()
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

        private fun <L : DispatchRequest, R : DispatchResponse> ArrayDeque<DispatchTransaction<L, R>>.take(
            other: ArrayDeque<DispatchTransaction<L, R>>
        ): ArrayDeque<DispatchTransaction<L, R>> {
            if (other.isEmpty()) return this
            this += other
            other.clear()
            return this
        }
    }
}
