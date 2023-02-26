package org.rsmod.plugins.api.cache.type.enums

import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeLiteral

private const val DEFAULT_ID = -1
private const val DEFAULT_KEY_TYPE = 'i'
private const val DEFAULT_VAL_TYPE = 'i'
private const val DEFAULT_INT_VALUE = -1
private const val DEFAULT_TRANSMIT_FLAG = false

private val DEFAULT_STR_VALUE: String? = null

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
    public var defaultStr: String? = DEFAULT_STR_VALUE,
    public var strValues: MutableMap<Int, String> = mutableMapOf(),
    public var intValues: MutableMap<Int, Int> = mutableMapOf(),
    public var size: Int = 0,
    public var transmit: Boolean = DEFAULT_TRANSMIT_FLAG
) {

    public fun build(): EnumType<Any, Any> {
        val keyType = EnumTypeIdentifier.values.firstOrNull { it.char == keyType }
            ?: error("EnumTypeIdentifier not declared (key=$keyType).")
        val valType = EnumTypeIdentifier.values.firstOrNull { it.char == valType }
            ?: error("EnumTypeIdentifier not declared (val=$valType).")

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
        keyType: EnumTypeIdentifier,
        valType: EnumTypeIdentifier,
        default: Any
    ) {
        val keyLiteral = keyType.literal as EnumTypeLiteral<Int, *>
        val valLiteral = valType.literal as EnumTypeLiteral<Int, *>
        values.forEach { (rawKey, rawValue) ->
            val key = keyLiteral.decode(rawKey)
                ?: error("Could not decode `$rawKey` with key literal `${keyLiteral.javaClass.simpleName}`.")
            val value = valLiteral.decode(rawValue)
            this[key] = value ?: default
        }
    }

    private fun MutableMap<Any, Any>.putStrProperties(
        values: Map<Int, String>,
        keyType: EnumTypeIdentifier,
        valType: EnumTypeIdentifier,
        default: Any
    ) {
        /* key literals are always integers */
        val keyLiteral = keyType.literal as EnumTypeLiteral<Int, *>
        val valLiteral = valType.literal as EnumTypeLiteral<String, *>
        values.forEach { (rawKey, rawValue) ->
            val key = keyLiteral.decode(rawKey)
                ?: error("Could not decode `$rawKey` with key literal `${keyLiteral.javaClass.simpleName}`.")
            val value = valLiteral.decode(rawValue)
            this[key] = value ?: default
        }
    }

    private fun EnumTypeIdentifier.defaultIntProperty(rawDefault: Int): Any? {
        val decoder = literal as EnumTypeLiteral<Int, *>
        return if (rawDefault == DEFAULT_INT_VALUE) null else decoder.decode(rawDefault)
    }

    private fun EnumTypeIdentifier.defaultStrProperty(rawDefault: String?): Any? {
        val decoder = literal as EnumTypeLiteral<String, *>
        return if (rawDefault == null || rawDefault == DEFAULT_STR_VALUE) {
            null
        } else {
            decoder.decode(rawDefault)
        }
    }
}
