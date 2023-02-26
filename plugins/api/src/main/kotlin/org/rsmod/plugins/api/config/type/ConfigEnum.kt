package org.rsmod.plugins.api.config.type

import org.rsmod.plugins.api.cache.type.enums.EnumType
import org.rsmod.plugins.api.cache.type.enums.EnumTypeBuilder
import org.rsmod.plugins.api.cache.type.enums.EnumTypeList
import org.rsmod.plugins.api.config.CacheTypeLiteralUtil.AUTO_INCREMENT_INT
import org.rsmod.plugins.api.config.CacheTypeLiteralUtil.TYPE_STRING_CONVERSION
import org.rsmod.plugins.api.config.CacheTypeLiteralUtil.convert
import org.rsmod.plugins.api.config.CacheTypeLiteralUtil.toConvertedEntryMap
import org.rsmod.plugins.types.NamedTypeMapHolder

public data class ConfigEnum(
    val id: Int,
    val name: String,
    val inherit: String?,
    val transmit: Boolean,
    val keyType: String,
    val valType: String,
    var default: Any?,
    var entries: MutableMap<Any, Any>?,
    val values: List<Any>?
) {

    @Suppress("UNUSED_PARAMETER")
    public fun toCacheType(
        names: NamedTypeMapHolder,
        types: EnumTypeList
    ): EnumType<Any, Any> {
        val builder = EnumTypeBuilder()
        val keyId = TYPE_STRING_CONVERSION.getValue(keyType)
        val valId = TYPE_STRING_CONVERSION.getValue(valType)
        val entries = (entries ?: mutableMapOf())
            .assertAutoIncrement(entries, values, keyType, valType)
            .putAutoIncrementEntries(values, keyType, valType)
            .toConvertedEntryMap(names, keyId, valId)
        val default = default?.convert(names, valId)
        builder.id = id
        builder.name = name
        builder.keyType = keyId.char
        builder.valType = valId.char
        builder.transmit = transmit
        builder.size = entries.size
        if (valId.isString) {
            check(default == null || default is String) { "`default` value must be a string." }
            default?.let { builder.defaultStr = valId.encodeString(it) }
            entries.forEach { (key, value) ->
                val encodedKey = keyId.encodeInt(key)
                val encodedValue = valId.encodeString(value)
                builder.strValues[encodedKey] = encodedValue
            }
        } else if (valId.isInt) {
            default?.let { builder.defaultInt = valId.encodeInt(it) }
            entries.forEach { (key, value) ->
                val encodedKey = keyId.encodeInt(key)
                val encodedValue = valId.encodeInt(value)
                builder.intValues[encodedKey] = encodedValue
            }
        }
        inherit?.let { TODO("Implement EnumTypeBuilder::plusAssign operator") }
        return builder.build()
    }

    private companion object {

        fun MutableMap<Any, Any>.putAutoIncrementEntries(
            values: List<Any>?,
            keyType: String,
            valType: String
        ): Map<Any, Any> {
            if (values.isNullOrEmpty()) return this
            val incrementKeys = arrayOfNulls<Any>(values.size)
            val incrementValues = arrayOfNulls<Any>(values.size)
            for (i in values.indices) {
                val keyElement = if (keyType == AUTO_INCREMENT_INT) i else values[i]
                val valElement = if (valType == AUTO_INCREMENT_INT) i else values[i]
                incrementKeys[i] = keyElement
                incrementValues[i] = valElement
            }
            for (i in values.indices) {
                val key = incrementKeys[i] ?: error("Key cannot be null.")
                val value = incrementValues[i] ?: error("Value cannot be null.")
                this[key] = value
            }
            return this
        }

        private fun MutableMap<Any, Any>.assertAutoIncrement(
            entries: MutableMap<Any, Any>?,
            values: List<Any>?,
            keyType: String,
            valType: String
        ): MutableMap<Any, Any> {
            if (keyType == AUTO_INCREMENT_INT || valType == AUTO_INCREMENT_INT) {
                if (entries.isNullOrEmpty()) return this
                error(
                    "Cannot define `entries` with auto-increment key or value types. " +
                        "Use `values` instead."
                )
            }
            if (values.isNullOrEmpty()) return this
            error(
                "Cannot define `values` with non auto-increment key or value types. " +
                    "Use `entries` instead."
            )
        }
    }
}
