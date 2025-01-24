package org.rsmod.objtx

import kotlin.math.max
import kotlin.math.min

@DslMarker private annotation class TransactionBuilder

@TransactionBuilder
public class Transaction<T>(
    public val input: (T?) -> TransactionObj?,
    public val output: (TransactionObj?) -> T?,
    public var autoCommit: Boolean = true,
) {
    public var certLookup: Map<Int, TransactionObjTemplate>? = null
    public var placeholderLookup: Map<Int, TransactionObjTemplate>? = null
    public var stackableLookup: Set<Int>? = null

    private val results = mutableListOf<TransactionResult>()
    private val inventories = mutableListOf<TransactionInventory<T>>()

    public fun results(): TransactionResultList<T> {
        return TransactionResultList(output, inventories, results)
    }

    public fun insert(init: InsertQuery.() -> Unit) {
        val query = InsertQuery().apply(init)
        execute(query)
    }

    public fun delete(init: DeleteQuery.() -> Unit) {
        val query = DeleteQuery().apply(init)
        execute(query)
    }

    public fun transfer(init: TransferQuery.() -> Unit) {
        val query = TransferQuery().apply(init)
        execute(query)
    }

    public fun swap(init: SwapQuery.() -> Unit) {
        val query = SwapQuery().apply(init)
        execute(query)
    }

    public fun shift(init: ShiftQuery.() -> Unit) {
        val query = ShiftQuery().apply(init)
        execute(query)
    }

    public fun dump(init: DumpQuery.() -> Unit) {
        val query = DumpQuery().apply(init)
        execute(query)
    }

    public fun execute(query: TransactionQuery) {
        val result = query.result()
        results += result
        if (result is TransactionResult.Err) {
            throw TransactionCancellation(result)
        }
    }

    public fun register(inventory: TransactionInventory<T>): TransactionInventory<T> {
        val contains = inventories.any { it.output === inventory.output }
        check(!contains) { "`inventory` has already been registered in transaction." }
        inventories += inventory
        return inventory
    }

    private fun certTemplate(obj: Int): TransactionObjTemplate? {
        return certLookup?.get(obj)
    }

    private fun placeholderTemplate(obj: Int): TransactionObjTemplate? {
        return placeholderLookup?.get(obj)
    }

    private fun stackable(obj: Int): Boolean {
        val lookup = stackableLookup ?: return false
        return obj in lookup
    }

    @TransactionBuilder
    public inner class InsertQuery(
        public var into: TransactionInventory<T>? = null,
        public var obj: Int? = null,
        public var strictCount: Int = 1,
        private var preferredCount: Int? = null,
        public var strictSlot: Int? = null,
        private var preferredSlot: Int = 0,
        public var vars: Int = 0,
        public var cert: Boolean = false,
        public var uncert: Boolean = false,
    ) : TransactionQuery {
        public var count: Int
            get() = preferredCount ?: 0
            set(value) {
                preferredCount = value
            }

        public var slot: Int
            get() = preferredSlot
            set(value) {
                preferredSlot = value
            }

        private val isStrictCount: Boolean
            get() = preferredCount == null

        private val solveSlot: Int
            get() = strictSlot ?: preferredSlot

        override fun result(): TransactionResult {
            val inv = into ?: return TransactionResult.Exception("`into` is required.")
            val obj = obj ?: return TransactionResult.Exception("`obj` is required.")
            val count = preferredCount ?: strictCount
            if (count < 1) {
                return TransactionResult.InvalidCountRequest
            }
            return inv.insert(obj, count)
        }

        private fun TransactionInventory<T>.insert(obj: Int, count: Int): TransactionResult {
            val template = this@Transaction.certTemplate(obj)
            if (template != null && vars > 0) {
                return TransactionResult.VarObjIncorrectlyHasCert
            }
            if (vars > 0) {
                return insertIndividual(obj, count)
            }
            if (template != null && template.isCert) {
                val uncert = uncert || stackAll
                val transform = if (uncert) template.link else obj
                return when {
                    stackNone -> insertIndividual(transform, count)
                    transform == obj || stackAll -> insertStack(transform, count)
                    else -> insertIndividual(transform, count)
                }
            }
            if (stackAll) {
                return insertStack(obj, count)
            } else if (stackNone) {
                return insertIndividual(obj, count)
            }
            if (template != null && !template.isCert && cert) {
                return if (stackNone) {
                    insertIndividual(template.link, count)
                } else {
                    insertStack(template.link, count)
                }
            }
            val stacks = this@Transaction.stackable(obj)
            if (stacks) {
                return insertStack(obj, count)
            }
            return insertIndividual(obj, count)
        }

        private fun TransactionInventory<T>.insertStack(obj: Int, count: Int): TransactionResult {
            val placeholderSlot = if (placeholders) findPlaceholderSlot(obj) else null
            if (placeholderSlot != null) {
                this[placeholderSlot] = TransactionObj(obj, count)
                return TransactionResult.Ok(requested = count, completed = count)
            }
            val previousSlot = indexOf(obj)
            if (previousSlot != null) {
                val prevCount = this[previousSlot]?.count ?: 0
                val sumCount = count.toLong() + prevCount
                val addCount = min(Int.MAX_VALUE.toLong() - prevCount, sumCount - prevCount).toInt()
                if (isStrictCount && addCount != count) {
                    return TransactionResult.NotEnoughSpace
                }
                this[previousSlot] = TransactionObj(obj, prevCount + addCount)
                return TransactionResult.Ok(requested = count, completed = addCount)
            }
            val strictSlot = strictSlot
            if (strictSlot != null && this[strictSlot] != null) {
                return TransactionResult.StrictSlotTaken
            }
            val emptySlot = indexOfNull(solveSlot) ?: return TransactionResult.NotEnoughSpace
            this[emptySlot] = TransactionObj(obj, count)
            return TransactionResult.Ok(requested = count, completed = count)
        }

        private fun TransactionInventory<T>.insertIndividual(
            obj: Int,
            count: Int,
        ): TransactionResult {
            val placeholderSlot = if (placeholders) findPlaceholderSlot(obj) else null
            if (placeholderSlot != null) {
                this[placeholderSlot] = TransactionObj(obj, count, vars)
                return TransactionResult.Ok(requested = count, completed = count)
            }
            val strictSlot = strictSlot
            if (strictSlot != null && this[strictSlot] != null) {
                return TransactionResult.StrictSlotTaken
            }
            val cappedCount = if (isStrictCount) count else min(freeSpace(), count)
            var completed = 0
            val startIndex = solveSlot
            val isStrictCount = isStrictCount
            for (i in 0 until cappedCount) {
                val slot = indexOfNull(startIndex + i)
                if (slot == null && isStrictCount) {
                    return TransactionResult.NotEnoughSpace
                } else if (slot == null) {
                    continue
                }
                completed++
                this[slot] = TransactionObj(obj, vars = vars)
            }
            return TransactionResult.Ok(requested = count, completed = completed)
        }

        private fun TransactionInventory<T>.findPlaceholderSlot(obj: Int): Int? {
            val template = this@Transaction.placeholderTemplate(obj)
            return if (template != null && !template.isPlaceholder) {
                indexOf(template.link)
            } else {
                null
            }
        }
    }

    @TransactionBuilder
    public inner class DeleteQuery(
        public var from: TransactionInventory<T>? = null,
        public var obj: Int? = null,
        public var strictCount: Int = 1,
        private var preferredCount: Int? = null,
        public var strictSlot: Int? = null,
        private var preferredSlot: Int = 0,
        public var placehold: Boolean = false,
    ) : TransactionQuery {
        public var count: Int
            get() = preferredCount ?: 0
            set(value) {
                preferredCount = value
            }

        public var slot: Int
            get() = preferredSlot
            set(value) {
                preferredSlot = value
            }

        private val isStrictCount: Boolean
            get() = preferredCount == null

        private val solveSlot: Int
            get() = strictSlot ?: preferredSlot

        override fun result(): TransactionResult {
            val inv = from ?: return TransactionResult.Exception("`from` is required.")
            val obj = obj ?: return TransactionResult.Exception("`obj` is required.")
            val count = preferredCount ?: strictCount
            if (count < 1) {
                return TransactionResult.InvalidCountRequest
            }
            return inv.delete(obj, count)
        }

        private fun TransactionInventory<T>.delete(obj: Int, count: Int): TransactionResult {
            if (stackAll) {
                return deleteStack(obj, count)
            }
            val stackable = this@Transaction.stackable(obj)
            if (stackable) {
                return deleteStack(obj, count)
            }
            val certTemplate = this@Transaction.certTemplate(obj)
            if (certTemplate != null && certTemplate.isCert) {
                return deleteStack(obj, count)
            }
            return deleteIndividual(obj, count)
        }

        private fun TransactionInventory<T>.deleteStack(obj: Int, count: Int): TransactionResult {
            val slot = indexOf(obj, solveSlot)
            if (slot == null && isStrictCount) {
                return TransactionResult.ObjNotFound
            } else if (slot == null) {
                return TransactionResult.Ok(requested = count, completed = 0)
            } else if (strictSlot != null && slot != strictSlot) {
                return TransactionResult.ObjNotFound
            } else if (this[slot]?.hasVars == true) {
                return deleteIndividual(obj, count)
            }
            val prevCount = this[slot]?.count ?: 0
            val newCount = max(0, prevCount - count)
            val removeCount = prevCount - newCount
            if (isStrictCount && removeCount != count) {
                return TransactionResult.NotEnoughObjCount
            }
            this[slot] = decrementCount(obj, newCount)
            return TransactionResult.Ok(requested = count, completed = removeCount)
        }

        private fun TransactionInventory<T>.deleteIndividual(
            obj: Int,
            count: Int,
        ): TransactionResult {
            val slots = indexesOf(obj, count, solveSlot)
            if (slots.isEmpty() && isStrictCount) {
                return TransactionResult.ObjNotFound
            } else if (strictSlot != null && slots.firstOrNull() != strictSlot) {
                return TransactionResult.ObjNotFound
            } else if (count > slots.size && isStrictCount) {
                return TransactionResult.NotEnoughObjCount
            } else if (slots.isEmpty()) {
                return TransactionResult.Ok(requested = count, completed = 0)
            }
            for (slot in slots) {
                val removeObj =
                    this[slot]
                        ?: return TransactionResult.Exception("Unexpected null obj at slot `$slot`")
                val newCount = removeObj.count - 1
                this[slot] = decrementCount(obj, newCount)
            }
            return TransactionResult.Ok(requested = count, completed = slots.size)
        }

        private fun decrementCount(obj: Int, newCount: Int): TransactionObj? {
            return if (newCount > 0) {
                TransactionObj(obj, newCount)
            } else if (placehold) {
                val placeholderTemplate = this@Transaction.placeholderTemplate(obj)
                if (placeholderTemplate != null && !placeholderTemplate.isPlaceholder) {
                    TransactionObj(placeholderTemplate.link)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    @TransactionBuilder
    public inner class TransferQuery(
        public var from: TransactionInventory<T>? = null,
        public var into: TransactionInventory<T>? = null,
        public var fromSlot: Int? = null,
        public var intoSlot: Int = 0,
        public var count: Int = 1,
        public var cert: Boolean = false,
        public var uncert: Boolean = false,
        public var placehold: Boolean = false,
    ) : TransactionQuery {
        override fun result(): TransactionResult {
            val from = from ?: return TransactionResult.Exception("`from` is required.")
            val into = into ?: return TransactionResult.Exception("`into` is required.")
            val fromSlot = fromSlot ?: return TransactionResult.Exception("`fromSlot` is required.")
            val obj = from[fromSlot] ?: return TransactionResult.ObjNotFound
            if (count < 1) {
                return TransactionResult.InvalidCountRequest
            } else if (obj.count < 1) {
                return TransactionResult.NotEnoughObjCount
            }
            // If only one count is requested, we have no need to calculate
            // total obj count in inventory.
            val count =
                if (count == 1) {
                    1
                } else if (obj.hasVars) {
                    min(from.totalVarCount(obj.id), count)
                } else {
                    min(from.totalCount(obj.id), count)
                }
            // When obj with var is selected and a count greater than 1 is requested,
            // we search for all objs of matching id individually so that their vars
            // can also be copied and transferred appropriately.
            if (count > 1 && obj.hasVars) {
                val slots = from.indexesOf(obj.id, count, fromSlot, mutableListOf())
                if (slots.isEmpty()) {
                    return TransactionResult.Exception(
                        "`slots` should never be empty at this point."
                    )
                }
                val multiVarSearch = from.multiVarTransfer(into, slots, count)
                return multiVarSearch
            }
            val simpleSearch = from.simpleTransfer(into, obj, count, fromSlot)
            return simpleSearch
        }

        private fun TransactionInventory<T>.simpleTransfer(
            into: TransactionInventory<T>,
            obj: TransactionObj,
            count: Int,
            fromSlot: Int,
        ): TransactionResult {
            val delete =
                this@Transaction.DeleteQuery(
                        from = this,
                        obj = obj.id,
                        strictCount = count,
                        placehold = placehold,
                        preferredSlot = fromSlot,
                    )
                    .result()
            if (delete is TransactionResult.Err) {
                return delete
            }
            val insert =
                this@Transaction.InsertQuery(
                        into = into,
                        obj = obj.id,
                        strictCount = count,
                        preferredSlot = intoSlot,
                        vars = obj.vars,
                        cert = cert,
                        uncert = uncert,
                    )
                    .result()
            if (insert is TransactionResult.Err) {
                return insert
            }
            return TransactionResult.Ok(requested = count, completed = count)
        }

        private fun TransactionInventory<T>.multiVarTransfer(
            into: TransactionInventory<T>,
            slots: Iterable<Int>,
            totalCount: Int,
        ): TransactionResult {
            var sumCount = 0
            for (slot in slots) {
                val obj =
                    this[slot]
                        ?: return TransactionResult.Exception(
                            "`slot` from given search gave null object slot."
                        )
                sumCount += obj.count
                if (sumCount > totalCount) {
                    return TransactionResult.Exception(
                        "MultiVar search attempted to delete too many objects."
                    )
                }
                val delete =
                    this@Transaction.DeleteQuery(
                            from = this,
                            obj = obj.id,
                            strictCount = obj.count,
                            placehold = placehold,
                            preferredSlot = slot,
                        )
                        .result()
                if (delete is TransactionResult.Err) {
                    return delete
                }
                val insert =
                    this@Transaction.InsertQuery(
                            into = into,
                            obj = obj.id,
                            strictCount = obj.count,
                            vars = obj.vars,
                            cert = cert,
                            uncert = uncert,
                            preferredSlot = intoSlot,
                        )
                        .result()
                if (insert is TransactionResult.Err) {
                    return insert
                }
            }
            return TransactionResult.Ok(requested = count, completed = count)
        }

        private fun TransactionInventory<T>.totalCount(obj: Int): Int {
            if (stackNone) {
                return image.count { it?.id == obj }
            } else if (stackAll) {
                val index = indexOf(obj) ?: return 0
                return this[index]?.count ?: 0
            }
            val stackable = this@Transaction.stackable(obj)
            if (stackable) {
                val index = indexOf(obj) ?: return 0
                return this[index]?.count ?: 0
            }
            val certTemplate = this@Transaction.certTemplate(obj)
            if (certTemplate != null && certTemplate.isCert) {
                val index = indexOf(obj) ?: return 0
                return this[index]?.count ?: 0
            }
            return image.count { it?.id == obj }
        }

        private fun TransactionInventory<T>.totalVarCount(obj: Int): Int {
            return image.count { it?.id == obj }
        }
    }

    @TransactionBuilder
    public inner class SwapQuery(
        public var from: TransactionInventory<T>? = null,
        public var into: TransactionInventory<T>? = null,
        public var fromSlot: Int? = null,
        public var intoSlot: Int? = null,
        public var cert: Boolean = false,
        public var uncert: Boolean = false,
        public var merge: Boolean = false,
        public var strict: Boolean = true,
    ) : TransactionQuery {
        override fun result(): TransactionResult {
            val from = from ?: return TransactionResult.Exception("`from` is required.")
            val into = into ?: return TransactionResult.Exception("`into` is required.")
            val fromSlot = fromSlot ?: return TransactionResult.Exception("`fromSlot` is required.")
            val obj = from[fromSlot] ?: return TransactionResult.ObjNotFound
            if (obj.count < 1) {
                return TransactionResult.NotEnoughObjCount
            }
            val intoSlot = intoSlot
            if (intoSlot == null) {
                // This type of transaction, where we do not care what slot the
                // obj gets placed into, is better suited as a transfer query.
                val transfer =
                    this@Transaction.TransferQuery(
                        from = from,
                        into = into,
                        fromSlot = fromSlot,
                        count = obj.count,
                        cert = cert,
                        uncert = uncert,
                        placehold = false,
                    )
                return transfer.result()
            }
            // If query is going from an inv that stacks non-stackable objs, into an inv that
            // does not - we take that into account and cap the transfer count to 1.
            val canStackInto = into.canStack(obj.id)
            val count = if (!canStackInto) 1 else obj.count
            val otherObj = into[intoSlot]
            return if (merge && otherObj?.id == obj.id) {
                merge(from, into, fromSlot, intoSlot, obj, otherObj, count, canStackInto)
            } else {
                swap(from, into, fromSlot, intoSlot, obj, otherObj, count)
            }
        }

        private fun swap(
            from: TransactionInventory<T>,
            into: TransactionInventory<T>,
            fromSlot: Int,
            intoSlot: Int,
            obj: TransactionObj,
            otherObj: TransactionObj?,
            count: Int,
        ): TransactionResult {
            val requested = count
            if (otherObj != null) {
                val deleteOtherObj =
                    this@Transaction.DeleteQuery(
                            from = into,
                            obj = otherObj.id,
                            strictCount = otherObj.count,
                            strictSlot = intoSlot,
                            placehold = false,
                        )
                        .result()
                if (deleteOtherObj is TransactionResult.Err) {
                    return deleteOtherObj
                }
            }
            val deleteObj =
                this@Transaction.DeleteQuery(
                        from = from,
                        obj = obj.id,
                        strictCount = count,
                        strictSlot = fromSlot,
                    )
                    .result()
            if (deleteObj is TransactionResult.Err) {
                return deleteObj
            }
            if (otherObj != null) {
                val insertOtherObj =
                    this@Transaction.InsertQuery(
                            into = from,
                            obj = otherObj.id,
                            strictCount = otherObj.count,
                            preferredSlot = if (strict) 0 else fromSlot,
                            strictSlot = if (strict) fromSlot else null,
                            vars = otherObj.vars,
                            cert = cert,
                            uncert = uncert,
                        )
                        .result()
                if (insertOtherObj is TransactionResult.Err) {
                    return insertOtherObj
                }
            }
            val insertObj =
                this@Transaction.InsertQuery(
                        into = into,
                        obj = obj.id,
                        strictCount = count,
                        strictSlot = intoSlot,
                        vars = obj.vars,
                        cert = cert,
                        uncert = uncert,
                    )
                    .result()
            if (insertObj is TransactionResult.Err) {
                return insertObj
            }
            return TransactionResult.Ok(requested = requested, completed = requested)
        }

        private fun merge(
            from: TransactionInventory<T>,
            into: TransactionInventory<T>,
            fromSlot: Int,
            intoSlot: Int,
            obj: TransactionObj,
            otherObj: TransactionObj,
            count: Int,
            canStackInto: Boolean,
        ): TransactionResult {
            if (obj.hasVars || otherObj.hasVars) {
                return swap(from, into, fromSlot, intoSlot, obj, otherObj, count)
            } else if (!canStackInto) {
                return swap(from, into, fromSlot, intoSlot, obj, otherObj, count)
            }
            val otherCount = otherObj.count
            val sumCount = otherCount.toLong() + count
            val addCount = min(Int.MAX_VALUE.toLong() - otherCount, sumCount - otherCount).toInt()
            if (addCount == 0) {
                // NOTE: it has not been confirmed what should happen in this situation, where
                // `into` already has max stack of the obj. For now, we have decided to just return
                // a [NotEnoughSpace] error result.
                return TransactionResult.NotEnoughSpace
            }
            into[intoSlot] = otherObj.copy(count = otherObj.count + addCount)
            from[fromSlot] =
                if (count > addCount) {
                    obj.copy(count = count - addCount)
                } else {
                    null
                }
            return TransactionResult.Ok(requested = count, completed = addCount)
        }

        private fun TransactionInventory<T>.canStack(obj: Int): Boolean {
            if (stackNone) {
                return false
            } else if (stackAll) {
                return true
            }
            val stackable = this@Transaction.stackable(obj)
            if (stackable) {
                return true
            }
            val certTemplate = this@Transaction.certTemplate(obj)
            return certTemplate != null && certTemplate.isCert
        }
    }

    @TransactionBuilder
    public inner class ShiftQuery(
        public var from: TransactionInventory<T>? = null,
        public var fromSlot: Int? = null,
        public var intoSlot: Int? = null,
    ) : TransactionQuery {
        override fun result(): TransactionResult {
            val from = from ?: return TransactionResult.Exception("`from` is required.")
            val fromSlot = fromSlot ?: return TransactionResult.Exception("`fromSlot` is required.")
            val intoSlot = intoSlot ?: return TransactionResult.Exception("`intoSlot` is required.")
            val obj = from[fromSlot] ?: return TransactionResult.ObjNotFound
            // When trying to shift an obj by placing it into an empty slot, the behaviour changes.
            // It results in the obj being swapped to the target slot instead of being shifted.
            if (from[intoSlot] == null) {
                val swap =
                    this@Transaction.SwapQuery(
                        from = from,
                        into = from,
                        fromSlot = fromSlot,
                        intoSlot = intoSlot,
                        cert = false,
                        uncert = false,
                        merge = false,
                    )
                return swap.result()
            }
            return if (intoSlot == fromSlot) {
                TransactionResult.Ok(requested = obj.count, completed = 0)
            } else if (fromSlot < intoSlot) {
                from.shiftComingFromLeft(obj.copy(), fromSlot, intoSlot)
            } else {
                from.shiftComingFromRight(obj.copy(), fromSlot, intoSlot)
            }
        }

        private fun TransactionInventory<T>.shiftComingFromLeft(
            copy: TransactionObj,
            fromSlot: Int,
            intoSlot: Int,
        ): TransactionResult {
            val shiftRange = (fromSlot + 1)..intoSlot
            for (slot in shiftRange) {
                this[slot - 1] = this[slot]
            }
            this[intoSlot] = copy
            return TransactionResult.Ok(requested = copy.count, completed = copy.count)
        }

        private fun TransactionInventory<T>.shiftComingFromRight(
            copy: TransactionObj,
            fromSlot: Int,
            intoSlot: Int,
        ): TransactionResult {
            val shiftRange = (intoSlot until fromSlot).reversed()
            for (slot in shiftRange) {
                this[slot + 1] = this[slot]
            }
            this[intoSlot] = copy
            return TransactionResult.Ok(requested = copy.count, completed = copy.count)
        }
    }

    @TransactionBuilder
    public inner class DumpQuery(
        public var from: TransactionInventory<T>? = null,
        public var into: TransactionInventory<T>? = null,
        public var cert: Boolean = false,
        public var uncert: Boolean = false,
        public var placehold: Boolean = false,
        public var keepSlots: Set<Int>? = null,
    ) : TransactionQuery {
        override fun result(): TransactionResult {
            val from = from ?: return TransactionResult.Exception("`from` is required.")
            val into = into ?: return TransactionResult.Exception("`into` is required.")
            return from.dumpInto(into, keepSlots)
        }

        private fun TransactionInventory<T>.dumpInto(
            into: TransactionInventory<T>,
            keepSlots: Set<Int>?,
        ): TransactionResult {
            var completed = 0
            var requested = 0
            var error: TransactionResult? = null
            for (i in image.indices) {
                val obj = image[i] ?: continue
                if (keepSlots != null && i in keepSlots) {
                    continue
                }
                if (obj.count > 1 && obj.hasVars) {
                    return TransactionResult.Exception(
                        "Var obj should not be stackable. (obj=$obj)"
                    )
                }
                val insert =
                    this@Transaction.InsertQuery(
                            into = into,
                            obj = obj.id,
                            preferredCount = obj.count,
                            vars = obj.vars,
                            cert = cert,
                            uncert = uncert,
                        )
                        .result()
                if (insert is TransactionResult.Err) {
                    error = error ?: insert
                    continue
                } else if (insert !is TransactionResult.Ok) {
                    continue
                }
                // If not a single obj was inserted without an error, we can safely
                // assume the query could not be completed due to space limitations.
                // Make sure we remain aware of this last error, and continue the
                // iteration for further queries.
                if (insert.completed == 0) {
                    error = TransactionResult.NotEnoughSpace
                    continue
                }
                val delete =
                    this@Transaction.DeleteQuery(
                            from = this,
                            obj = obj.id,
                            strictCount = insert.completed,
                            strictSlot = i,
                            placehold = placehold,
                        )
                        .result()
                if (delete is TransactionResult.Err) {
                    // Since the obj was added to `into` inventory, we ensure that the
                    // delete query goes through as well, otherwise, return the error.
                    return delete
                }
                requested += insert.requested
                completed += insert.completed
            }
            if (error != null && completed == 0) {
                return error
            }
            return TransactionResult.Ok(requested, completed)
        }
    }

    public fun interface TransactionQuery {
        public fun result(): TransactionResult
    }

    private companion object {
        private val TransactionObjTemplate.isCert: Boolean
            get() = template != 0

        private val TransactionObjTemplate.isPlaceholder: Boolean
            get() = template != 0
    }
}
