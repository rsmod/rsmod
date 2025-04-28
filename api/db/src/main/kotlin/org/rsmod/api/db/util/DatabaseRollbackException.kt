package org.rsmod.api.db.util

import java.lang.RuntimeException

public class DatabaseRollbackException(
    transactionCause: Throwable,
    public val rollbackCause: Throwable,
) : RuntimeException(buildMessage(transactionCause, rollbackCause), transactionCause) {
    private companion object {
        private fun buildMessage(transaction: Throwable, rollback: Throwable): String =
            "Database transaction failed with '${transaction::class.simpleName}'; " +
                "rollback also failed with '${rollback::class.simpleName}'."
    }
}
