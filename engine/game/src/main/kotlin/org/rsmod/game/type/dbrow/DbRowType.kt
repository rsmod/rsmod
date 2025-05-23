package org.rsmod.game.type.dbrow

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class DbRowType : CacheType()

public data class HashedDbRowType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, DbRowType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "DbRowType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedDbRowType) return false
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

public data class UnpackedDbRowType(
    public val table: Int,
    public val data: Map<Int, List<Any>>,
    public val types: Map<Int, List<Int>>,
    public val columnCount: Int,
    override var internalId: Int?,
    override var internalName: String?,
) : DbRowType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun toHashedType(): HashedDbRowType =
        HashedDbRowType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = data.keys.hashCode().toLong()
        result = 61 * result + types.hashCode()
        result = 61 * result + table
        result = 61 * result + id
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedDbRowType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "table=$table, " +
            "data=$data, " +
            "types=$types, " +
            "columnCount=$columnCount" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedDbRowType) return false
        if (internalId != other.internalId) return false
        if (table != other.table) return false
        if (columnCount != other.columnCount) return false
        if (data != other.data) return false
        if (types != other.types) return false
        return true
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + columnCount
        result = 31 * result + table
        result = 31 * result + types.hashCode()
        result = 31 * result + (internalId ?: 0)
        return result
    }
}
