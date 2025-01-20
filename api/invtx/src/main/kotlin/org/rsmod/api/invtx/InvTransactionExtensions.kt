package org.rsmod.api.invtx

import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.objs
import org.rsmod.api.player.output.updateInvRecommended
import org.rsmod.api.repo.obj.ObjRepository
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.inv.InvStackType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.map.CoordGrid
import org.rsmod.objtx.Transaction
import org.rsmod.objtx.TransactionCancellation
import org.rsmod.objtx.TransactionInventory
import org.rsmod.objtx.TransactionResult
import org.rsmod.objtx.TransactionResultList

public fun Player.invAddOrDrop(
    repo: ObjRepository,
    type: ObjType,
    count: Int = 1,
    duration: Int = this.lootDropDuration ?: constants.lootdrop_duration,
    coords: CoordGrid = this.coords,
    inv: Inventory = this.inv,
): Boolean {
    val transaction = invAdd(inv, type, count = count)
    if (transaction.success) {
        return true
    }
    val obj = Obj(coords, type, count, currentMapClock, this)
    repo.add(obj, duration)
    return false
}

public fun Player.invTakeFee(fee: Int, inv: Inventory = this.inv): Boolean {
    val transaction = invDel(inv, objs.coins, count = fee)
    return transaction.success
}

public fun Player.invClear(inv: Inventory) {
    if (inv.isNotEmpty() && !denyProtectedAccess(inv)) {
        inv.fillNulls()
        updateModifiedInv(inv)
    }
}

public fun Player.invCommit(inv: Inventory, transaction: TransactionResultList<InvObj>) {
    transaction.commitAll()
    if (transaction.anyCompleted()) {
        updateModifiedInv(inv)
    }
}

public fun Player.invAdd(
    inv: Inventory,
    obj: Int,
    count: Int,
    vars: Int = 0,
    slot: Int? = null,
    strict: Boolean = true,
    cert: Boolean = false,
    uncert: Boolean = false,
    updateInv: Boolean = true,
    autoCommit: Boolean = true,
): TransactionResultList<InvObj> =
    invTransaction(inv, updateInv, autoCommit) {
        val targetInv = select(inv)
        add(
            inv = targetInv,
            obj = obj,
            count = count,
            vars = vars,
            slot = slot,
            strict = strict,
            cert = cert,
            uncert = uncert,
        )
    }

public fun Player.invAdd(
    inv: Inventory,
    type: ObjType,
    count: Int = 1,
    vars: Int = 0,
    slot: Int? = null,
    strict: Boolean = true,
    cert: Boolean = false,
    uncert: Boolean = false,
    updateInv: Boolean = true,
    autoCommit: Boolean = true,
): TransactionResultList<InvObj> =
    invAdd(
        inv = inv,
        obj = type.id,
        count = count,
        vars = vars,
        slot = slot,
        strict = strict,
        cert = cert,
        uncert = uncert,
        updateInv = updateInv,
        autoCommit = autoCommit,
    )

public fun Transaction<InvObj>.add(
    inv: TransactionInventory<InvObj>,
    obj: Int,
    count: Int,
    vars: Int = 0,
    slot: Int? = null,
    strict: Boolean = true,
    cert: Boolean = false,
    uncert: Boolean = false,
) {
    insert {
        this.into = inv
        this.obj = obj
        this.cert = cert
        this.uncert = uncert
        this.vars = vars
        if (strict) {
            this.strictCount = count
            this.strictSlot = slot
        } else {
            this.count = count
            this.slot = slot ?: 0
        }
    }
}

public fun Player.invAddAll(
    inv: Inventory,
    objs: Iterable<InvObj>,
    startSlot: Int? = null,
    strict: Boolean = true,
    cert: Boolean = false,
    uncert: Boolean = false,
    updateInv: Boolean = true,
    autoCommit: Boolean = true,
): TransactionResultList<InvObj> =
    invTransaction(inv, updateInv, autoCommit) {
        val targetInv = select(inv)
        var targetSlot = startSlot ?: 0
        for (obj in objs) {
            add(
                inv = targetInv,
                obj = obj.id,
                count = obj.count,
                vars = obj.vars,
                slot = targetSlot++,
                strict = strict,
                cert = cert,
                uncert = uncert,
            )
        }
    }

public fun Player.invDel(
    inv: Inventory,
    obj: Int,
    count: Int,
    slot: Int? = null,
    strict: Boolean = true,
    placehold: Boolean = false,
    updateInv: Boolean = true,
    autoCommit: Boolean = true,
): TransactionResultList<InvObj> =
    invTransaction(inv, updateInv, autoCommit) {
        val targetInv = select(inv)
        delete(
            inv = targetInv,
            obj = obj,
            count = count,
            slot = slot,
            strict = strict,
            placehold = placehold,
        )
    }

public fun Player.invDel(
    inv: Inventory,
    type: ObjType,
    count: Int = 1,
    slot: Int? = null,
    strict: Boolean = true,
    placehold: Boolean = false,
    updateInv: Boolean = true,
    autoCommit: Boolean = true,
): TransactionResultList<InvObj> =
    invDel(
        inv = inv,
        obj = type.id,
        count = count,
        slot = slot,
        strict = strict,
        placehold = placehold,
        updateInv = updateInv,
        autoCommit = autoCommit,
    )

public fun Transaction<InvObj>.delete(
    inv: TransactionInventory<InvObj>,
    obj: Int,
    count: Int,
    slot: Int? = null,
    strict: Boolean = true,
    placehold: Boolean = false,
) {
    delete {
        this.from = inv
        this.obj = obj
        this.placehold = placehold
        if (strict) {
            this.strictCount = count
            this.strictSlot = slot
        } else {
            this.count = count
            this.slot = slot ?: 0
        }
    }
}

public fun Player.invDelAll(
    inv: Inventory,
    objs: Iterable<InvObj>,
    startSlot: Int? = null,
    strict: Boolean = true,
    placehold: Boolean = false,
    updateInv: Boolean = true,
    autoCommit: Boolean = true,
): TransactionResultList<InvObj> =
    invTransaction(inv, updateInv, autoCommit) {
        val targetInv = select(inv)
        var targetSlot = startSlot ?: 0
        for (obj in objs) {
            delete(
                inv = targetInv,
                obj = obj.id,
                count = obj.count,
                slot = targetSlot++,
                strict = strict,
                placehold = placehold,
            )
        }
    }

/**
 * @param into The inventory to place the obj into. `null` if swap is to occur in `this` Inventory.
 * @param cert If the obj in the respective [fromSlot] should be turned into its certificate form
 *   when inserted into [into] inv. (if applicable)
 * @param uncert If the obj in the respective [fromSlot] should be transformed, when applicable,
 *   from its certificate form to its non-certificate variant.
 * @param mergeStacks If the obj in the respective [fromSlot] should be merged with any exact obj
 *   match found in [into] inv. (ex: merging stackable items when moved from bank to inventory)
 * @param strict When `true`: If any obj exists in [intoSlot] it must strictly try to take slot
 *   [fromSlot]. When `false`: The `into` obj will attempt to take [fromSlot], but if it cannot, it
 *   will find the next free slot. _`from` obj will always be strictly taken from [fromSlot] and the
 *   transaction will fail if it cannot be done._
 */
public fun Player.invSwap(
    from: Inventory,
    fromSlot: Int,
    intoSlot: Int,
    into: Inventory? = null,
    cert: Boolean = false,
    uncert: Boolean = false,
    mergeStacks: Boolean = true,
    strict: Boolean = true,
    updateInv: Boolean = true,
    autoCommit: Boolean = true,
): TransactionResultList<InvObj> =
    invTransaction(from, into, updateInv, autoCommit) {
        val fromInv = select(from)
        val intoInv = into?.let { select(it) } ?: fromInv
        swap(
            from = fromInv,
            fromSlot = fromSlot,
            into = intoInv,
            intoSlot = intoSlot,
            cert = cert,
            uncert = uncert,
            mergeStacks = mergeStacks,
            strict = strict,
        )
    }

/** See [invSwap] extension function for documentation. */
public fun Transaction<InvObj>.swap(
    from: TransactionInventory<InvObj>,
    fromSlot: Int,
    into: TransactionInventory<InvObj>,
    intoSlot: Int,
    cert: Boolean = false,
    uncert: Boolean = false,
    mergeStacks: Boolean = true,
    strict: Boolean = true,
) {
    swap {
        this.from = from
        this.into = into
        this.fromSlot = fromSlot
        this.intoSlot = intoSlot
        this.cert = cert
        this.uncert = uncert
        this.merge = mergeStacks
        this.strict = strict
    }
}

public fun Player.invTransfer(
    from: Inventory,
    fromSlot: Int,
    count: Int,
    into: Inventory,
    intoSlot: Int? = null,
    cert: Boolean = false,
    uncert: Boolean = false,
    placehold: Boolean = false,
    updateInv: Boolean = true,
    autoCommit: Boolean = true,
): TransactionResultList<InvObj> {
    check(into != from) { "`into` should not be equal to `from` inv. Use `swap` function instead." }
    return invTransaction(from, into, updateInv, autoCommit) {
        val fromInv = select(from)
        val intoInv = select(into)
        transfer(
            from = fromInv,
            fromSlot = fromSlot,
            count = count,
            into = intoInv,
            intoSlot = intoSlot,
            cert = cert,
            uncert = uncert,
            placehold = placehold,
        )
    }
}

public fun Transaction<InvObj>.transfer(
    from: TransactionInventory<InvObj>,
    fromSlot: Int,
    count: Int,
    into: TransactionInventory<InvObj>,
    intoSlot: Int? = null,
    cert: Boolean = false,
    uncert: Boolean = false,
    placehold: Boolean = false,
) {
    transfer {
        this.from = from
        this.into = into
        this.fromSlot = fromSlot
        this.intoSlot = intoSlot ?: 0
        this.count = count
        this.cert = cert
        this.uncert = uncert
        this.placehold = placehold
    }
}

public fun Transaction<InvObj>.select(inv: Inventory): TransactionInventory<InvObj> {
    val image = Array(inv.objs.size) { input(inv.objs[it]) }
    val stack = inv.type.stack.toTransactionStackType()
    val transformed =
        TransactionInventory(stack, inv.objs, image, inv.type.placeholders, inv.modifiedSlots)
    register(transformed)
    return transformed
}

public fun Player.invTransaction(
    from: Inventory,
    into: Inventory?,
    updateInv: Boolean = true,
    autoCommit: Boolean = true,
    transaction: Transaction<InvObj>.() -> Unit,
): TransactionResultList<InvObj> {
    if (denyProtectedAccess(from) || into != null && denyProtectedAccess(into)) {
        return protectedAccessException()
    }
    val result = transactions.transaction(autoCommit) { transaction() }
    if (updateInv && result.commited) {
        if (from.hasModifiedSlots()) {
            updateModifiedInv(from)
        }
        if (into != null && into.hasModifiedSlots()) {
            updateModifiedInv(into)
        }
    }
    return result
}

public fun Player.invTransaction(
    inv: Inventory,
    updateInv: Boolean = true,
    autoCommit: Boolean = true,
    transaction: Transaction<InvObj>.() -> Unit,
): TransactionResultList<InvObj> =
    invTransaction(
        from = inv,
        into = null,
        updateInv = updateInv,
        autoCommit = autoCommit,
        transaction = transaction,
    )

private fun Player.updateModifiedInv(inv: Inventory) {
    updateInvRecommended(inv)
    inv.clearModifiedSlots()
}

private fun Player.denyProtectedAccess(inv: Inventory): Boolean =
    inv.type.protect && isAccessProtected

private fun Player.protectedAccessException(): TransactionResultList<InvObj> {
    val exception = TransactionResult.Exception("Player does not have protected access: $this")
    return transactions.transaction(autoCommit = true) { throw TransactionCancellation(exception) }
}

private fun InvStackType.toTransactionStackType(): TransactionInventory.StackType =
    when (this) {
        InvStackType.Normal -> TransactionInventory.NormalStack
        InvStackType.Always -> TransactionInventory.AlwaysStack
        InvStackType.Never -> TransactionInventory.NeverStack
    }

private val transactions: InvTransactions
    get() = cachedInventoryTransactions
