package org.rsmod.game.type.param

import kotlin.reflect.KClass
import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType
import org.rsmod.game.type.literal.CacheVarLiteral

public sealed class ParamType<T> : CacheType() {
    internal abstract var typedDefault: T?

    public val default: T?
        get() = typedDefault
}

public data class HashedParamType<T : Any>(
    public val type: KClass<T>,
    override var startHash: Long?,
    override var typedDefault: T?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, ParamType<T>() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "ParamType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash, " +
            "supposedType=${type.simpleName}" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedParamType<*>) return false
        if (startHash != other.startHash) return false
        if (internalId != other.internalId) return false
        if (type != other.type) return false
        return true
    }

    override fun hashCode(): Int {
        var result = internalId?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + (startHash?.hashCode() ?: 0)
        return result
    }
}

public data class UnpackedParamType<T : Any>(
    public val type: KClass<T>?,
    public val typeLiteral: CacheVarLiteral?,
    public val defaultInt: Int?,
    public val defaultStr: String?,
    public val autoDisable: Boolean,
    public val transmit: Boolean,
    override var typedDefault: T?,
    override var internalId: Int?,
    override var internalName: String?,
) : ParamType<T>() {
    public fun computeIdentityHash(): Long {
        var result = (typeLiteral?.char?.hashCode()?.toLong() ?: 0)
        result = 61 * result + (defaultInt?.hashCode() ?: 0)
        result = 61 * result + (defaultStr?.hashCode() ?: 0)
        result = 61 * result + autoDisable.hashCode()
        result = 61 * result + (default?.hashCode() ?: 0)
        result = 61 * result + (internalId?.hashCode() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedParamType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "typeLiteral=$typeLiteral, " +
            "default=$default, " +
            "defaultInt=$defaultInt, " +
            "defaultStr=$defaultStr, " +
            "transmit=$transmit, " +
            "autoDisable=$autoDisable" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedParamType<*>) return false
        if (typeLiteral != other.typeLiteral) return false
        if (defaultInt != other.defaultInt) return false
        if (defaultStr != other.defaultStr) return false
        if (autoDisable != other.autoDisable) return false
        if (transmit != other.transmit) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}
