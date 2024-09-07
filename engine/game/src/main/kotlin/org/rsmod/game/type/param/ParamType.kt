package org.rsmod.game.type.param

import kotlin.reflect.KClass
import org.rsmod.game.type.literal.CacheVarLiteral

public sealed class ParamType<T>(
    internal var internalId: Int?,
    internal var internalName: String?,
    internal var typedDefault: T?,
) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String?
        get() = internalName

    public val default: T?
        get() = typedDefault
}

public class HashedParamType<T : Any>(
    public val type: KClass<T>,
    internal var startHash: Long? = null,
    typedDefault: T?,
    internalId: Int? = null,
    internalName: String? = null,
) : ParamType<T>(internalId, internalName, typedDefault) {
    public val supposedHash: Long?
        get() = startHash

    override fun toString(): String =
        "ParamType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash, " +
            "givenType=${type.simpleName}" +
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

public class UnpackedParamType<T : Any>(
    public val type: KClass<T>?,
    public val typeLiteral: CacheVarLiteral?,
    public val defaultInt: Int?,
    public val defaultStr: String?,
    public val autoDisable: Boolean,
    typedDefault: T?,
    internalId: Int,
    internalName: String,
) : ParamType<T>(internalId, internalName, typedDefault) {
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
            "autoDisable=$autoDisable" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedParamType<*>) return false

        if (typeLiteral != other.typeLiteral) return false
        if (defaultInt != other.defaultInt) return false
        if (defaultStr != other.defaultStr) return false
        if (autoDisable != other.autoDisable) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}
