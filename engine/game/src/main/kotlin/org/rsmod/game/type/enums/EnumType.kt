package org.rsmod.game.type.enums

import kotlin.reflect.KClass
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.CacheType
import org.rsmod.game.type.HashedCacheType
import org.rsmod.game.type.literal.CacheVarLiteral
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.isAssociatedWith

public sealed class EnumType<K, V> : CacheType()

public data class HashedEnumType<K : Any, V : Any>(
    public val keyType: KClass<K>,
    public val valType: KClass<V>,
    override var startHash: Long?,
    override var internalName: String?,
    override var internalId: Int? = null,
) : HashedCacheType, EnumType<K, V>() {
    public val autoResolve: Boolean = startHash == null

    override fun toString(): String =
        "EnumType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "supposedHash=$supposedHash, " +
            "keyType=$keyType, " +
            "valType=$valType" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HashedEnumType<*, *>) return false
        if (keyType != other.keyType) return false
        if (valType != other.valType) return false
        if (startHash != other.startHash) return false
        if (internalId != other.internalId) return false
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

public data class UnpackedEnumType<K : Any, V : Any>(
    public val keyType: KClass<K>,
    public val valType: KClass<V>,
    public val keyLiteral: CacheVarLiteral,
    public val valLiteral: CacheVarLiteral,
    public val primitiveMap: Map<Any, Any?>,
    public val defaultStr: String?,
    public val defaultInt: Int?,
    public val transmit: Boolean,
    internal var default: V?,
    internal var typedMap: Map<K, V?>,
    override var internalId: Int?,
    override var internalName: String?,
) : EnumType<K, V>(), Map<K, V?> {
    private val identityHash by lazy { computeIdentityHash() }

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

    public fun getValue(key: K): V =
        get(key) ?: throw NoSuchElementException("Key $key is missing in the map.")

    override fun get(key: K): V? = typedMap[key] ?: default

    override fun isEmpty(): Boolean = typedMap.isEmpty()

    override fun containsValue(value: V?): Boolean = typedMap.containsValue(value)

    override fun containsKey(key: K): Boolean = typedMap.containsKey(key)

    public fun toHashedType(): HashedEnumType<K, V> =
        HashedEnumType(
            keyType = keyType,
            valType = valType,
            startHash = identityHash,
            internalName = internalName,
            internalId = internalId,
        )

    public fun computeIdentityHash(): Long {
        var result = internalId?.hashCode()?.toLong() ?: 0L
        result = 61 * result + keyLiteral.char.hashCode()
        result = 61 * result + valLiteral.char.hashCode()
        return result and 0x7FFFFFFFFFFFFFFF
    }

    override fun toString(): String =
        "UnpackedEnumType(" +
            "internalName='$internalName', " +
            "internalId=$internalId, " +
            "keyLiteral=$keyLiteral, " +
            "valLiteral=$valLiteral, " +
            "default=$default, " +
            "defaultInt=$defaultInt, " +
            "defaultStr=$defaultStr, " +
            "entries=$typedMap, " +
            "transmit=$transmit" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UnpackedEnumType<*, *>) return false
        if (default != other.default) return false
        if (keyLiteral != other.keyLiteral) return false
        if (valLiteral != other.valLiteral) return false
        if (primitiveMap != other.primitiveMap) return false
        if (transmit != other.transmit) return false
        if (internalId != other.internalId) return false
        return true
    }

    override fun hashCode(): Int = computeIdentityHash().toInt()
}

public fun <V : Any> UnpackedEnumType<ObjType, V>.findOrNull(obj: InvObj): V? {
    for ((key, value) in this) {
        if (key.isAssociatedWith(obj)) {
            return value
        }
    }
    return null
}

public fun <V : Any> UnpackedEnumType<ObjType, V>.find(obj: InvObj): V =
    findOrNull(obj) ?: throw NoSuchElementException("Key $obj is missing in the map.")
