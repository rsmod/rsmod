package org.rsmod.game.type.clientscript

import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType

public sealed class ClientScriptType : CacheType()

public data class HashedClientScriptType(
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, ClientScriptType() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "ClientScriptType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedClientScriptType) return false
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

public data class UnpackedClientScriptType(
    public val intLocalCount: Int,
    public val stringLocalCount: Int,
    public val intArgumentCount: Int,
    public val stringArgumentCount: Int,
    public val intOperands: IntArray,
    public val stringOperands: Array<String?>,
    public val commands: IntArray,
    public val switches: Array<Map<Int, Int>>,
    override var internalId: Int?,
    override var internalName: String?,
) : ClientScriptType() {
    private val identityHash by lazy { computeIdentityHash() }

    public fun toHashedType(): HashedClientScriptType =
        HashedClientScriptType(
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = intLocalCount.toLong()
        result = 61 * result + stringLocalCount
        result = 61 * result + intArgumentCount
        result = 61 * result + stringArgumentCount
        result = 61 * result + intOperands.contentHashCode()
        result = 61 * result + stringOperands.contentHashCode()
        result = 61 * result + commands.contentHashCode()
        result = 61 * result + switches.contentHashCode()
        result = 61 * result + (internalId ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String {
        return "UnpackedClientScriptType(" +
            "internalName=$internalName, " +
            "internalId=$internalId, " +
            "intLocalCount=$intLocalCount, " +
            "stringLocalCount=$stringLocalCount, " +
            "intArgumentCount=$intArgumentCount, " +
            "stringArgumentCount=$stringArgumentCount, " +
            "intOperands=${intOperands.contentToString()}, " +
            "stringOperands=${stringOperands.contentToString()}, " +
            "commands=${commands.contentToString()}, " +
            "switches=$switches" +
            ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedClientScriptType) return false
        if (internalId != other.internalId) return false
        if (intLocalCount != other.intLocalCount) return false
        if (stringLocalCount != other.stringLocalCount) return false
        if (intArgumentCount != other.intArgumentCount) return false
        if (stringArgumentCount != other.stringArgumentCount) return false
        if (!intOperands.contentEquals(other.intOperands)) return false
        if (!stringOperands.contentEquals(other.stringOperands)) return false
        if (!commands.contentEquals(other.commands)) return false
        if (!switches.contentEquals(other.switches)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = intLocalCount
        result = 31 * result + stringLocalCount
        result = 31 * result + intArgumentCount
        result = 31 * result + stringArgumentCount
        result = 31 * result + intOperands.contentHashCode()
        result = 31 * result + stringOperands.contentHashCode()
        result = 31 * result + commands.contentHashCode()
        result = 31 * result + switches.contentHashCode()
        result = 31 * result + (internalId ?: 0)
        return result
    }
}
