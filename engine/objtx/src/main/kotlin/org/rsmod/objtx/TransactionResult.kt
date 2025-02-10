package org.rsmod.objtx

import kotlin.contracts.contract
import org.rsmod.objtx.TransactionResult.Ok

public fun TransactionResult?.isOk(): Boolean {
    contract { returns(true) implies (this@isOk is Ok) }
    return this is Ok
}

public fun TransactionResult?.isErr(): Boolean {
    contract { returns(true) implies (this@isErr is TransactionResult.Err) }
    return this is TransactionResult.Err
}

public sealed class TransactionResult {
    public data class Ok(public val requested: Int, public val completed: Int) :
        TransactionResult() {
        public val left: Int
            get() = requested - completed

        public val fullSuccess: Boolean
            get() = completed == requested

        public val emptySuccess: Boolean
            get() = !fullSuccess && completed == 0

        public val partialSuccess: Boolean
            get() = !fullSuccess && completed in 1..<requested
    }

    public sealed class Err : TransactionResult()

    public data class Exception(public val message: String? = null) : Err()

    public data object InvalidCountRequest : Err()

    public data object VarObjIncorrectlyHasCert : Err()

    public data object NotEnoughSpace : Err()

    public data object ObjNotFound : Err()

    public data object NotEnoughObjCount : Err()

    public data object StrictSlotTaken : Err()

    public data object RestrictedDummyitem : Err()
}
