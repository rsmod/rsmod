package org.rsmod.game.type.enums

import kotlin.reflect.KClass
import org.rsmod.game.type.literal.CacheVarLiteral
import org.rsmod.game.type.util.GenericPropertySelector.select
import org.rsmod.game.type.util.GenericPropertySelector.selectMap

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
    public var defaultStr: String? = null
    public var defaultInt: Int? = null
    public var primitiveMap: Map<Any, Any?>? = null
    public var typedMap: Map<K, V?>? = null
    public var transmit: Boolean? = null

    public fun build(id: Int): UnpackedEnumType<K, V>? {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val keyLiteral = CacheVarLiteral.forCharId(keyCharId) ?: return null
        val valLiteral = CacheVarLiteral.forCharId(valCharId) ?: return null
        val primitiveMap = primitiveMap ?: emptyMap()
        val typedMap = typedMap ?: emptyMap()
        val transmit = transmit ?: DEFAULT_TRANSMIT
        return UnpackedEnumType(
            keyType = keyType,
            valType = valType,
            keyLiteral = keyLiteral,
            valLiteral = valLiteral,
            primitiveMap = primitiveMap,
            default = default,
            defaultStr = defaultStr,
            defaultInt = defaultInt,
            typedMap = typedMap,
            transmit = transmit,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object {
        public const val DEFAULT_TRANSMIT: Boolean = true

        public fun merge(
            edit: UnpackedEnumType<*, *>,
            base: UnpackedEnumType<*, *>,
        ): UnpackedEnumType<Any, Any> {
            val keyType = select(edit, base, default = null) { keyType } as KClass<Any>
            val valType = select(edit, base, default = null) { valType } as KClass<Any>
            val keyLiteral = select(edit, base, default = null) { keyLiteral }
            val valLiteral = select(edit, base, default = null) { valLiteral }
            val primitiveMap = selectMap(edit, base) { primitiveMap }
            val typedMap = selectMap(edit, base) { typedMap } as? Map<Any, Any?>
            val default = select(edit, base, default = null) { default }
            val defaultInt = select(edit, base, default = null) { defaultInt }
            val defaultStr = select(edit, base, default = null) { defaultStr }
            val transmit = select(edit, base, DEFAULT_TRANSMIT) { transmit }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedEnumType(
                keyType = keyType,
                valType = valType,
                keyLiteral = keyLiteral,
                valLiteral = valLiteral,
                primitiveMap = primitiveMap,
                defaultStr = defaultStr,
                defaultInt = defaultInt,
                default = default,
                typedMap = typedMap ?: emptyMap(),
                transmit = transmit,
                internalId = internalId,
                internalName = internalName,
            )
        }
    }
}
