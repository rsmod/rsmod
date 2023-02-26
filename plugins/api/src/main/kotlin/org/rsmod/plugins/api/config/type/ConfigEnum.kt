package org.rsmod.plugins.api.config.type

import org.rsmod.plugins.api.cache.type.enums.EnumType
import org.rsmod.plugins.api.cache.type.enums.EnumTypeBuilder
import org.rsmod.plugins.api.cache.type.enums.EnumTypeList
import org.rsmod.plugins.api.cache.type.literal.CacheTypeBaseInt
import org.rsmod.plugins.api.cache.type.literal.CacheTypeBaseString
import org.rsmod.plugins.api.config.CacheTypeIdentifierUtil.AUTO_INCREMENT_INT
import org.rsmod.plugins.api.config.CacheTypeIdentifierUtil.TYPE_STRING_CONVERSION
import org.rsmod.plugins.api.config.CacheTypeIdentifierUtil.convert
import org.rsmod.plugins.api.config.CacheTypeIdentifierUtil.toConvertedEntryMap
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

    @Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
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
            // As of now - keys are always int-based
            val keyLiteral = keyId.literal as CacheTypeBaseInt<in Any>
            val valLiteral = valId.literal as CacheTypeBaseString<in Any>
            default?.let { builder.defaultStr = valLiteral.encode(it) }
            entries.forEach { (key, value) ->
                val encodedKey = keyLiteral.encode(key)
                val encodedValue = valLiteral.encode(value)
                builder.strValues[encodedKey] = encodedValue
            }
        } else if (valId.isInt) {
            // As of now - keys are always int-based
            val keyLiteral = keyId.literal as CacheTypeBaseInt<in Any>
            val valLiteral = valId.literal as CacheTypeBaseInt<in Any>
            default?.let { builder.defaultInt = valLiteral.encode(it) }
            entries.forEach { (key, value) ->
                val encodedKey = keyLiteral.encode(key)
                val encodedValue = valLiteral.encode(value)
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
