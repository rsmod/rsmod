package org.rsmod.content.interfaces.bank.util

import org.rsmod.game.inv.InvObj
import org.rsmod.objtx.Transaction

internal inline fun Transaction<InvObj>.leftShift(
    init: Transaction<InvObj>.LeftShiftQuery.() -> Unit
) {
    val query = LeftShiftQuery().apply(init)
    execute(query)
}

internal inline fun Transaction<InvObj>.rightShift(
    init: Transaction<InvObj>.RightShiftQuery.() -> Unit
) {
    val query = RightShiftQuery().apply(init)
    execute(query)
}

internal inline fun Transaction<InvObj>.bulkShift(
    init: Transaction<InvObj>.BulkShiftQuery.() -> Unit
) {
    val query = BulkShiftQuery().apply(init)
    execute(query)
}

internal inline fun Transaction<InvObj>.shiftInsert(
    init: Transaction<InvObj>.ShiftInsertQuery.() -> Unit
) {
    val query = ShiftInsertQuery().apply(init)
    execute(query)
}
