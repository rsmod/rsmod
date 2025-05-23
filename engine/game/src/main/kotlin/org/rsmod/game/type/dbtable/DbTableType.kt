package org.rsmod.game.type.dbtable

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class DbTableType : CacheType()

public data class HashedDbTableType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, DbTableType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "DbTableType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedDbTableType) return false
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

public data class UnpackedDbTableType(
    public val types: Map<Int, List<Int>>,
    public val defaults: Map<Int, List<Any>>,
    public val attributes: Map<Int, Int>,
    // Transient buffer of all associated column tables used to verify cache edits and builds.
    public val columnTables: Set<Int>,
    public val columnCount: Int,
    override var internalId: Int?,
    override var internalName: String?,
) : DbTableType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun clientSide(): Boolean {
        return attributes.values.any { it and CLIENTSIDE != 0 }
    }

    public fun toHashedType(): HashedDbTableType =
        HashedDbTableType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = types.hashCode().toLong()
        result = 61 * result + id
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedDbTableType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "types=$types, " +
            "defaults=$defaults, " +
            "columnCount=$columnCount" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedDbTableType) return false
        if (internalId != other.internalId) return false
        if (columnCount != other.columnCount) return false
        if (types != other.types) return false
        if (defaults != other.defaults) return false
        if (attributes != other.attributes) return false
        return true
    }

    override fun hashCode(): Int {
        var result = types.hashCode()
        result = 31 * result + defaults.hashCode()
        result = 31 * result + attributes.hashCode()
        result = 31 * result + columnCount
        result = 31 * result + (internalId ?: 0)
        return result
    }

    public companion object {
        // The first 7 bits are used for the column id.
        public const val REQUIRED: Int = 0x80
        // The order of the bit flags below are not confirmed.
        public const val CLIENTSIDE: Int = 0x100
        public const val INDEXED: Int = 0x200
        public const val LIST: Int = 0x400
    }
}
