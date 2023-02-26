package org.rsmod.plugins.api.cache.type.enums

import org.rsmod.plugins.api.cache.type.literal.CacheTypeLiteral
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeCodec

private const val DEFAULT_ID = -1
private const val DEFAULT_KEY_TYPE = 'i'
private const val DEFAULT_VAL_TYPE = 'i'
private const val DEFAULT_INT_VALUE = -1
private const val DEFAULT_TRANSMIT_FLAG = false

@DslMarker
private annotation class BuilderDslMarker

@Suppress("UNCHECKED_CAST")
@BuilderDslMarker
public class EnumTypeBuilder(
    public var id: Int = DEFAULT_ID,
    public var name: String? = null,
    public var keyType: Char = DEFAULT_KEY_TYPE,
    public var valType: Char = DEFAULT_VAL_TYPE,
    public var defaultInt: Int = DEFAULT_INT_VALUE,
    public var defaultStr: String? = null,
    public var strValues: MutableMap<Int, String> = mutableMapOf(),
    public var intValues: MutableMap<Int, Int> = mutableMapOf(),
    public var size: Int = 0,
    public var transmit: Boolean = DEFAULT_TRANSMIT_FLAG
) {

    public fun build(): EnumType<Any, Any> {
        val keyType = CacheTypeLiteral.mapped[keyType]
            ?: error("Cache literal not mapped for char `$keyType)`.")
        val valType = CacheTypeLiteral.mapped[valType]
            ?: error("Cache literal not mapped for char `$valType)`.")

        check(keyType.isInt) {
            "Enums are restricted to Integer-based input/keys. (enum=$id, key=$keyType, val=$valType)"
        }

        if (valType.isString && intValues.isNotEmpty()) {
            error("EnumType(key=$keyType, val=$valType) has string value type - yet contains integer properties.")
        } else if (valType.isInt && strValues.isNotEmpty()) {
            error("EnumType(key=$keyType, val=$valType) has integer value type - yet contains string properties.")
        }

        return when {
            valType.isString -> {
                val properties = mutableMapOf<Any, Any>()
                val default = valType.defaultStrProperty(defaultStr)
                properties.putStrProperties(strValues, keyType, valType, (default ?: defaultStr ?: ""))
                EnumType(id, name, transmit, keyType, valType, default, properties)
            }
            valType.isInt -> {
                val properties = mutableMapOf<Any, Any>()
                val default = valType.defaultIntProperty(defaultInt)
                properties.putIntProperties(intValues, keyType, valType, default ?: defaultInt)
                EnumType(id, name, transmit, keyType, valType, default, properties)
            }
            else -> error("Unhandled value type `$valType` for enum $id.")
        }
    }

    private fun MutableMap<Any, Any>.putIntProperties(
        values: Map<Int, Int>,
        keyType: CacheTypeLiteral,
        valType: CacheTypeLiteral,
        default: Any
    ) {
        values.forEach { (rawKey, rawValue) ->
            val key = keyType.decodeInt(rawKey)
                ?: error("Could not decode `$rawKey` with key literal `$keyType`.")
            val value = valType.decodeInt(rawValue)
            this[key] = value ?: default
        }
    }

    private fun MutableMap<Any, Any>.putStrProperties(
        values: Map<Int, String>,
        keyType: CacheTypeLiteral,
        valType: CacheTypeLiteral,
        default: Any
    ) {
        values.forEach { (rawKey, rawValue) ->
            val key = keyType.decodeInt(rawKey)
                ?: error("Could not decode `$rawKey` with key literal `$keyType`.")
            val value = valType.decodeString(rawValue)
            this[key] = value ?: default
        }
    }

    private fun CacheTypeLiteral.defaultIntProperty(rawDefault: Int): Any? = if (rawDefault == DEFAULT_INT_VALUE) {
        null
    } else {
        val decoder = codec as CacheTypeCodec<Int, *>
        decoder.decode(rawDefault)
    }

    private fun CacheTypeLiteral.defaultStrProperty(rawDefault: String?): Any? = if (rawDefault == null) {
        null
    } else {
        val decoder = codec as CacheTypeCodec<String, *>
        decoder.decode(rawDefault)
    }
}
