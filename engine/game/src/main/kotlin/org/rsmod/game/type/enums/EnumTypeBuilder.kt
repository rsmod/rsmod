package org.rsmod.game.type.enums

import kotlin.reflect.KClass
import org.rsmod.game.type.literal.CacheVarLiteral

@DslMarker private annotation class EnumBuilderDsl

@EnumBuilderDsl
public class EnumTypeBuilder<K : Any, V : Any>(
    public val keyType: KClass<K>,
    public val valType: KClass<V>,
    public var internal: String? = null,
) {
    public var keyCharId: Char? = null
    public var valCharId: Char? = null
    public var default: V? = null
    public var primitiveMap: Map<Any, Any?>? = null
    public var typedMap: Map<K, V?>? = null

    public fun build(id: Int): UnpackedEnumType<K, V>? {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val keyLiteral = CacheVarLiteral.forCharId(keyCharId) ?: return null
        val valLiteral = CacheVarLiteral.forCharId(valCharId) ?: return null
        val primitiveMap = primitiveMap ?: emptyMap()
        val typedMap = typedMap ?: emptyMap()
        return UnpackedEnumType(
            keyType = keyType,
            valType = valType,
            keyLiteral = keyLiteral,
            valLiteral = valLiteral,
            primitiveMap = primitiveMap,
            default = default,
            typedMap = typedMap,
            internalId = id,
            internalName = internal,
        )
    }
}
