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
    public var transformLookup: Map<Int, TransactionObjTemplate>? = null
    public var stackableLookup: Set<Int>? = null
    public var dummyitemLookup: Set<Int>? = null

    private val results = mutableListOf<TransactionResult>()
    private val inventories = mutableListOf<TransactionInventory<T>>()

    public fun results(): TransactionResultList<T> {
        return TransactionResultList(output, inventories, results)
    }

    public inline fun insert(init: InsertQuery.() -> Unit) {
        val query = InsertQuery().apply(init)
        execute(query)
    }

    public inline fun delete(init: DeleteQuery.() -> Unit) {
        val query = DeleteQuery().apply(init)
        execute(query)
    }

    public inline fun transfer(init: TransferQuery.() -> Unit) {
        val query = TransferQuery().apply(init)
        execute(query)
    }

    public inline fun swap(init: SwapQuery.() -> Unit) {
        val query = SwapQuery().apply(init)
        execute(query)
    }

    public inline fun dump(init: DumpQuery.() -> Unit) {
        val query = DumpQuery().apply(init)
        execute(query)
    }

    public inline fun compact(init: CompactQuery.() -> Unit) {
        val query = CompactQuery().apply(init)
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

    private fun transformTemplate(obj: Int): TransactionObjTemplate? {
        return transformLookup?.get(obj)
    }

    private fun stackable(obj: Int): Boolean {
        val lookup = stackableLookup ?: return false
        return obj in lookup
    }

    private fun isDummyitem(obj: Int): Boolean {
        val lookup = dummyitemLookup ?: return false
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
        public var capacity: Int? = null,
        public var vars: Int = 0,
        public var cert: Boolean = false,
        public var uncert: Boolean = false,
        public var transform: Boolean = false,
        public var untransform: Boolean = false,
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
            val capacity = capacity ?: inv.image.size
            if (capacity > inv.image.size) {
                return TransactionResult.Exception(
                    "`capacity` cannot be greater than inventory size. " +
                        "(capacity=$capacity, invSize=${inv.image.size})"
                )
            }
            if (transform) {
                val template = this@Transaction.transformTemplate(obj)
                val resolved =
                    if (template != null && !template.isTransformed) {
                        template.link
                    } else {
                        obj
                    }
                return inv.insert(resolved, count, capacity)
            }
            if (untransform) {
                val template = this@Transaction.transformTemplate(obj)
                val resolved =
                    if (template != null && template.isTransformed) {
                        template.link
                    } else {
                        obj
                    }
                return inv.insert(resolved, count, capacity)
            }
            return inv.insert(obj, count, capacity)
        }

        private fun TransactionInventory<T>.insert(
            obj: Int,
            count: Int,
            capacity: Int,
        ): TransactionResult {
            val template = this@Transaction.certTemplate(obj)
            if (template != null && vars > 0) {
                return TransactionResult.VarObjIncorrectlyHasCert
            }
            if (vars > 0) {
                return insertIndividual(obj, count, capacity)
            }
            if (template != null && template.isCert) {
                val uncert = uncert || stackAll
                val transform = if (uncert) template.link else obj
                return when {
                    stackNone -> insertIndividual(transform, count, capacity)
                    transform == obj || stackAll -> insertStack(transform, count, capacity)
                    else -> insertIndividual(transform, count, capacity)
                }
            }
            if (stackAll) {
                return insertStack(obj, count, capacity)
            } else if (stackNone) {
                return insertIndividual(obj, count, capacity)
            }
            if (template != null && !template.isCert && cert) {
                return if (stackNone) {
                    insertIndividual(template.link, count, capacity)
                } else {
                    insertStack(template.link, count, capacity)
                }
            }
            val stacks = this@Transaction.stackable(obj)
            if (stacks) {
                return insertStack(obj, count, capacity)
            }
            return insertIndividual(obj, count, capacity)
        }

        private fun TransactionInventory<T>.insertStack(
            obj: Int,
            count: Int,
            capacity: Int,
        ): TransactionResult {
            val isDummyitem = this@Transaction.isDummyitem(obj)
            if (isDummyitem) {
                return TransactionResult.RestrictedDummyitem
            }
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
            val emptySlot =
                indexOfNull(solveSlot, capacity) ?: return TransactionResult.NotEnoughSpace
            if (capacity != image.size && occupiedSpace() >= capacity) {
                return TransactionResult.NotEnoughSpace
            }
            this[emptySlot] = TransactionObj(obj, count)
            return TransactionResult.Ok(requested = count, completed = count)
        }

        private fun TransactionInventory<T>.insertIndividual(
            obj: Int,
            count: Int,
            capacity: Int,
        ): TransactionResult {
            val isDummyitem = this@Transaction.isDummyitem(obj)
            if (isDummyitem) {
                return TransactionResult.RestrictedDummyitem
            }
            val placeholderSlot = if (placeholders) findPlaceholderSlot(obj) else null
            if (placeholderSlot != null) {
                this[placeholderSlot] = TransactionObj(obj, count, vars)
                return TransactionResult.Ok(requested = count, completed = count)
            }
            val strictSlot = strictSlot
            if (strictSlot != null && this[strictSlot] != null) {
                return TransactionResult.StrictSlotTaken
            }
            val freeSpace = freeSpace() - (image.size - capacity)
            val cappedCount = if (isStrictCount) count else min(freeSpace, count)
            var completed = 0
            val startIndex = solveSlot
            val isStrictCount = isStrictCount
            for (i in 0 until cappedCount) {
                val slot = indexOfNull(startIndex + i, capacity)
                if (slot == null && isStrictCount) {
                    return TransactionResult.NotEnoughSpace
                } else if (slot == null) {
                    continue
                }
                if (freeSpace - completed <= 0 && isStrictCount) {
                    return TransactionResult.NotEnoughSpace
                } else if (freeSpace - completed <= 0) {
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
        public var intoCapacity: Int? = null,
        public var cert: Boolean = false,
        public var uncert: Boolean = false,
        public var placehold: Boolean = false,
        public var transform: Boolean = false,
        public var untransform: Boolean = false,
        public var strict: Boolean = true,
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
            val insert =
                this@Transaction.InsertQuery(
                        into = into,
                        obj = obj.id,
                        preferredCount = if (strict) null else count,
                        strictCount = if (strict) count else 1,
                        preferredSlot = intoSlot,
                        capacity = intoCapacity,
                        vars = obj.vars,
                        cert = cert,
                        uncert = uncert,
                        transform = transform,
                        untransform = untransform,
                    )
                    .result()
            if (insert !is TransactionResult.Ok) {
                return insert
            }
            // When `strict` is false, the `delete` query can fail due to insufficient space in the
            // `into` inventory. Attempting to "delete" the obj from the `from` inventory in this
            // case would result in an `InvalidCountRequest` because a `strictCount` of `0` is not
            // valid. To handle this gracefully, we intercept and return `NotEnoughSpace` instead,
            // which is more intuitive for the consumer.
            if (!strict && insert.completed == 0 && count > 0) {
                return TransactionResult.NotEnoughSpace
            }
            val delete =
                this@Transaction.DeleteQuery(
                        from = this,
                        obj = obj.id,
                        strictCount = insert.completed,
                        placehold = placehold,
                        preferredSlot = fromSlot,
                    )
                    .result()
            if (delete !is TransactionResult.Ok) {
                return delete
            }
            return TransactionResult.Ok(requested = count, completed = delete.completed)
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
                            capacity = intoCapacity,
                            vars = obj.vars,
                            cert = cert,
                            uncert = uncert,
                            preferredSlot = intoSlot,
                            transform = transform,
                            untransform = untransform,
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
        public var transform: Boolean = false,
        public var untransform: Boolean = false,
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
                        transform = transform,
                        untransform = untransform,
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
                            // Assume that any obj that is swapped from the target inventory should
                            // have the opposite transformation flags.
                            transform = untransform,
                            untransform = transform,
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
                        transform = transform,
                        untransform = untransform,
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
    public inner class DumpQuery(
        public var from: TransactionInventory<T>? = null,
        public var into: TransactionInventory<T>? = null,
        public var cert: Boolean = false,
        public var uncert: Boolean = false,
        public var transform: Boolean = false,
        public var untransform: Boolean = false,
        public var placehold: Boolean = false,
        public var keepSlots: Set<Int>? = null,
        public var intoStartSlot: Int = 0,
        public var intoCapacity: Int? = null,
    ) : TransactionQuery {
        override fun result(): TransactionResult {
            val from = from ?: return TransactionResult.Exception("`from` is required.")
            val into = into ?: return TransactionResult.Exception("`into` is required.")
            return from.dumpInto(into, intoStartSlot, keepSlots)
        }

        private fun TransactionInventory<T>.dumpInto(
            into: TransactionInventory<T>,
            intoStartSlot: Int,
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
                            preferredSlot = intoStartSlot,
                            capacity = intoCapacity,
                            vars = obj.vars,
                            cert = cert,
                            uncert = uncert,
                            transform = transform,
                            untransform = untransform,
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

    @TransactionBuilder
    public inner class CompactQuery(
        public var from: TransactionInventory<T>? = null,
        public var startSlot: Int? = null,
        public var endSlot: Int? = null,
    ) : TransactionQuery {
        override fun result(): TransactionResult {
            val from = from ?: return TransactionResult.Exception("`from` is required.")
            val image = from.image
            val startSlot = startSlot ?: 0
            val endSlot = endSlot ?: (image.size - 1)
            if (startSlot !in image.indices) {
                return TransactionResult.Exception(
                    "`startSlot` not in valid range: $startSlot (size=${image.size})"
                )
            }
            if (endSlot !in image.indices) {
                return TransactionResult.Exception(
                    "`endSlot` not in valid range: $endSlot (size=${image.size})"
                )
            }
            if (endSlot <= startSlot) {
                return TransactionResult.Exception(
                    "`endSlot` must come after `startSlot`: " +
                        "endSlot=$endSlot, startSlot=$startSlot"
                )
            }
            return from.compact(startSlot..endSlot)
        }

        private fun TransactionInventory<T>.compact(slots: IntRange): TransactionResult {
            var emptySlot = -1
            for (slot in slots) {
                if (this[slot] == null) {
                    if (emptySlot == -1) {
                        emptySlot = slot
                    }
                } else if (emptySlot != -1) {
                    this[emptySlot] = this[slot]
                    this[slot] = null
                    emptySlot++
                }
            }
            val requested = slots.last - slots.first
            return TransactionResult.Ok(requested = requested, completed = requested)
        }
    }

    @TransactionBuilder
    public inner class ShiftInsertQuery(
        public var from: TransactionInventory<T>? = null,
        public var fromSlot: Int? = null,
        public var intoSlot: Int? = null,
    ) : TransactionQuery {
        override fun result(): TransactionResult {
            val from = from ?: return TransactionResult.Exception("`from` is required.")
            val fromSlot = fromSlot ?: return TransactionResult.Exception("`fromSlot` is required.")
            val intoSlot = intoSlot ?: return TransactionResult.Exception("`intoSlot` is required.")
            val obj = from[fromSlot] ?: return TransactionResult.ObjNotFound
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
    public inner class LeftShiftQuery(
        public var from: TransactionInventory<T>? = null,
        public var startSlot: Int? = null,
        public var toSlot: Int? = null,
        public var maxSlot: Int? = null,
        public var strict: Boolean = true,
    ) : TransactionQuery {
        override fun result(): TransactionResult {
            val from = from ?: return TransactionResult.Exception("`from` is required.")
            val startSlot =
                startSlot ?: return TransactionResult.Exception("`startSlot` is required.")
            val toSlot = toSlot ?: return TransactionResult.Exception("`toSlot` is required.")
            val image = from.image
            if (startSlot !in image.indices) {
                return TransactionResult.Exception(
                    "`startSlot` not in valid range: $startSlot (size=${image.size})"
                )
            }
            if (toSlot !in image.indices) {
                return TransactionResult.Exception(
                    "`toSlot` not in valid range: $toSlot (size=${image.size})"
                )
            }
            if (toSlot > startSlot) {
                return TransactionResult.Exception(
                    "`toSlot` must come before `startSlot`: target=$toSlot, start=$startSlot"
                )
            }
            val maxSlot = maxSlot ?: image.size
            if (maxSlot > image.size) {
                return TransactionResult.Exception(
                    "`maxSlot` cannot be greater than inventory capacity: " +
                        "maxSlot=$maxSlot, capacity=${image.size}"
                )
            }
            // When `strict` flag is set, we ensure that no non-null objs will be overwritten by
            // the shift.
            if (strict) {
                val nonNullObjSlot = (toSlot until startSlot).firstOrNull { from[it] != null }
                if (nonNullObjSlot != null) {
                    return TransactionResult.StrictSlotTaken
                }
            }
            return from.shift(startSlot, toSlot, maxSlot)
        }

        private fun TransactionInventory<T>.shift(
            startSlot: Int,
            toSlot: Int,
            maxSlot: Int,
        ): TransactionResult {
            val shiftElements = image.copyOfRange(startSlot, maxSlot)
            for (i in shiftElements.indices) {
                this[toSlot + i] = shiftElements[i]
            }
            val excessIndex = toSlot + shiftElements.size
            for (i in excessIndex until maxSlot) {
                if (this[i] != null) {
                    this[i] = null
                }
            }
            val requested = maxSlot - toSlot
            return TransactionResult.Ok(requested = requested, completed = requested)
        }
    }

    @TransactionBuilder
    public inner class RightShiftQuery(
        public var from: TransactionInventory<T>? = null,
        public var startSlot: Int? = null,
        public var shiftCount: Int? = null,
    ) : TransactionQuery {
        override fun result(): TransactionResult {
            val from = from ?: return TransactionResult.Exception("`from` is required.")
            val startSlot =
                startSlot ?: return TransactionResult.Exception("`startSlot` is required.")
            val shiftCount =
                shiftCount ?: return TransactionResult.Exception("`shiftCount` is required.")
            val image = from.image
            if (startSlot !in image.indices) {
                return TransactionResult.Exception(
                    "`startSlot` not in valid range: $startSlot (size=${image.size})"
                )
            }
            if (shiftCount <= 0) {
                return TransactionResult.Exception(
                    "`shiftCount` must be greater than zero: $shiftCount"
                )
            }
            return from.shift(startSlot, shiftCount)
        }

        private fun TransactionInventory<T>.shift(
            startSlot: Int,
            shiftCount: Int,
        ): TransactionResult {
            // Ensure that any non-null objs at the tail of the inventory do not overflow.
            for (i in image.size - 1 downTo image.size - shiftCount) {
                if (this[i] != null && i + shiftCount >= image.size) {
                    return TransactionResult.NotEnoughSpace
                }
            }
            for (i in image.size - 1 downTo startSlot + shiftCount) {
                val copy = image[i - shiftCount]
                if (copy == null && this[i] == null) {
                    continue
                }
                this[i] = copy
            }
            for (i in startSlot until startSlot + shiftCount) {
                this[i] = null
            }
            val requested = image.size - startSlot
            return TransactionResult.Ok(requested = requested, completed = requested)
        }
    }

    @TransactionBuilder
    public inner class BulkShiftQuery(
        public var from: TransactionInventory<T>? = null,
        public var fromSlots: IntRange? = null,
        public var intoSlot: Int? = null,
    ) : TransactionQuery {
        override fun result(): TransactionResult {
            val from = from ?: return TransactionResult.Exception("`from` is required.")
            val fromSlots =
                fromSlots ?: return TransactionResult.Exception("`fromSlots` is required.")
            val intoSlot = intoSlot ?: return TransactionResult.Exception("`intoSlot` is required.")
            return when {
                intoSlot > fromSlots.last -> from.shiftComingFromLeft(fromSlots, intoSlot)
                intoSlot < fromSlots.first -> from.shiftComingFromRight(fromSlots, intoSlot)
                else ->
                    TransactionResult.Exception(
                        "`intoSlot` should not be within range of `fromSlots`. " +
                            "(fromSlots=$fromSlots, intoSlot=$intoSlot)"
                    )
            }
        }

        private fun TransactionInventory<T>.shiftComingFromLeft(
            fromSlots: IntRange,
            intoSlot: Int,
        ): TransactionResult {
            val copied = image.copyOfRange(fromSlots.first, fromSlots.last + 1)

            val blockSize = fromSlots.last - fromSlots.first + 1
            for (slot in (fromSlots.last + 1)..intoSlot) {
                this[slot - blockSize] = this[slot]
            }

            val gapSize = intoSlot - fromSlots.last
            val targetStart = fromSlots.first + gapSize
            for (i in copied.indices) {
                this[targetStart + i] = copied[i]
            }

            return TransactionResult.Ok(requested = blockSize, completed = blockSize)
        }

        private fun TransactionInventory<T>.shiftComingFromRight(
            fromSlots: IntRange,
            intoSlot: Int,
        ): TransactionResult {
            val copied = image.copyOfRange(fromSlots.first, fromSlots.last + 1)

            val blockSize = fromSlots.last - fromSlots.first + 1
            for (slot in (fromSlots.first - 1) downTo intoSlot) {
                this[slot + blockSize] = this[slot]
            }

            for (i in copied.indices) {
                this[intoSlot + i] = copied[i]
            }

            return TransactionResult.Ok(requested = blockSize, completed = blockSize)
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

        private val TransactionObjTemplate.isTransformed: Boolean
            get() = template != 0
    }
}
