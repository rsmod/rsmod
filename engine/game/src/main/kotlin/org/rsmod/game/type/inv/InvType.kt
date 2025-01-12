package org.rsmod.game.type.inv

public sealed class InvType(internal var internalId: Int?, internal var internalName: String?) {
    public val internalNameGet: String?
        get() = internalName

    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")
}

public class HashedInvType(
    internal var startHash: Long? = null,
    internalId: Int? = null,
    internalName: String? = null,
) : InvType(internalId, internalName) {
    public val supposedHash: Long?
        get() = startHash

    override fun toString(): String =
        "InvType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedInvType) return false

        if (startHash != other.startHash) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = internalId?.hashCode() ?: 0
        result = 31 * result + (startHash?.hashCode() ?: 0)
        return result
    }
}

public class UnpackedInvType(
    public val scope: InvScope,
    public val stack: InvStackType,
    public val size: Int,
    public val flags: Int,
    public val stock: Array<InvStock?>?,
    internalId: Int,
    internalName: String,
) : InvType(internalId, internalName) {
    public val restock: Boolean
        get() = flags and RESTOCK_FLAG != 0

    public val allStock: Boolean
        get() = flags and ALL_STOCK_FLAG != 0

    public val protect: Boolean
        get() = flags and PROTECT_FLAG != 0

    public val runWeight: Boolean
        get() = flags and RUN_WEIGHT_FLAG != 0

    public val dummyInv: Boolean
        get() = flags and DUMMY_INV_FLAG != 0

    public val placeholders: Boolean
        get() = flags and PLACEHOLDERS_FLAG != 0

    public constructor(
        scope: InvScope,
        stack: InvStackType,
        size: Int,
        restock: Boolean,
        allStock: Boolean,
        protect: Boolean,
        runWeight: Boolean,
        dummyInv: Boolean,
        placeholders: Boolean,
        stock: Array<InvStock?>?,
        internalId: Int,
        internalName: String,
    ) : this(
        scope = scope,
        stack = stack,
        size = size,
        flags =
            pack(
                protect = protect,
                allStock = allStock,
                restock = restock,
                runWeight = runWeight,
                dummyInv = dummyInv,
                placeholders = placeholders,
            ),
        stock = stock,
        internalId = internalId,
        internalName = internalName,
    )

    public fun computeIdentityHash(): Long {
        var result = scope.id.toLong()
        result = 61 * result + stack.id
        result = 61 * result + size
        result = 61 * result + flags
        result = 61 * result + (stock?.contentHashCode() ?: 0)
        result = 61 * result + (internalId?.hashCode() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String {
        return "UnpackedInvType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "scope=$scope, " +
            "stack=$stack, " +
            "size=$size, " +
            "flags=$flags, " +
            "restock=$restock, " +
            "allStock=$allStock, " +
            "protect=$protect, " +
            "runWeight=$runWeight, " +
            "dummyInv=$dummyInv, " +
            "placeholders=$placeholders, " +
            "stock=${stock?.contentToString()}" +
            ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedInvType) return false

        if (scope != other.scope) return false
        if (stack != other.stack) return false
        if (size != other.size) return false
        if (flags != other.flags) return false
        if (stock != null) {
            if (other.stock == null) return false
            if (!stock.contentEquals(other.stock)) return false
        } else if (other.stock != null) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()

    public companion object {
        public const val PROTECT_FLAG: Int = 0x1
        public const val ALL_STOCK_FLAG: Int = 0x2
        public const val RESTOCK_FLAG: Int = 0x4
        public const val RUN_WEIGHT_FLAG: Int = 0x8
        public const val DUMMY_INV_FLAG: Int = 0x10
        public const val PLACEHOLDERS_FLAG: Int = 0x20

        internal fun pack(
            protect: Boolean,
            allStock: Boolean,
            restock: Boolean,
            runWeight: Boolean,
            dummyInv: Boolean,
            placeholders: Boolean,
        ): Int {
            var flags = 0
            if (protect) {
                flags = flags or PROTECT_FLAG
            }
            if (allStock) {
                flags = flags or ALL_STOCK_FLAG
            }
            if (restock) {
                flags = flags or RESTOCK_FLAG
            }
            if (runWeight) {
                flags = flags or RUN_WEIGHT_FLAG
            }
            if (dummyInv) {
                flags = flags or DUMMY_INV_FLAG
            }
            if (placeholders) {
                flags = flags or PLACEHOLDERS_FLAG
            }
            return flags
        }
    }
}
