package org.rsmod.game.type.enums

import kotlin.reflect.KClass
import org.rsmod.game.type.literal.CacheVarLiteral

public sealed class EnumType<K, V>(
    internal var internalId: Int?,
    internal var internalName: String?,
) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String?
        get() = internalName
}

public class HashedEnumType<K : Any, V : Any>(
    public val keyType: KClass<K>,
    public val valType: KClass<V>,
    internal var startHash: Long? = null,
    internalId: Int? = null,
    internalName: String? = null,
) : EnumType<K, V>(internalId, internalName) {
    public val supposedHash: Long?
        get() = startHash

    override fun toString(): String =
        "EnumType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash," +
            "keyType=$keyType, " +
            "valType=$valType" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedEnumType<*, *>) return false

        if (startHash != other.startHash) return false
        if (keyType != other.keyType) return false
        if (valType != other.valType) return false
        if (internalId != other.internalId) return false
        if (internalName != other.internalName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keyType.hashCode()
        result = 31 * result + valType.hashCode()
        result = 31 * result + (startHash?.hashCode() ?: 0)
        result = 31 * result + (internalId?.hashCode() ?: 0)
        return result
    }
}

public class UnpackedEnumType<K : Any, V : Any>(
    public val keyType: KClass<K>,
    public val valType: KClass<V>,
    public val keyLiteral: CacheVarLiteral,
    public val valLiteral: CacheVarLiteral,
    public val primitiveMap: Map<Any, Any?>,
    internal var default: V?,
    internal var typedMap: Map<K, V?>,
    internalId: Int? = null,
    internalName: String? = null,
) : EnumType<K, V>(internalId, internalName), Map<K, V?> {
    override val entries: Set<Map.Entry<K, V?>>
        get() = typedMap.entries

    override val keys: Set<K>
        get() = typedMap.keys

    override val size: Int
        get() = typedMap.size

    override val values: Collection<V?>
        get() = typedMap.values

    public val defaultValue: V?
        get() = default

    public fun getOrNull(key: K): V? = typedMap[key]

    public fun computeIdentityHash(): Long {
        var result = default?.hashCode()?.toLong() ?: 0L
        result = 61 * result + keyLiteral.char.hashCode()
        result = 61 * result + valLiteral.char.hashCode()
        result = 61 * result + primitiveMap.hashCode()
        result = 61 * result + (internalId?.hashCode() ?: 0)
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedEnumType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "keyLiteral=$keyLiteral, " +
            "valLiteral=$valLiteral, " +
            "default=$default, " +
            "entries=$typedMap" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedEnumType<*, *>) return false

        if (default != other.default) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()

    override fun isEmpty(): Boolean = typedMap.isEmpty()

    override fun get(key: K): V? = typedMap[key] ?: default

    override fun containsValue(value: V?): Boolean = typedMap.containsValue(value)

    override fun containsKey(key: K): Boolean = typedMap.containsKey(key)
}
