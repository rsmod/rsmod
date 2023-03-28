package org.rsmod.plugins.cache.packer.type

import org.rsmod.plugins.api.cache.type.enums.EnumTypeList
import org.rsmod.plugins.cache.config.enums.EnumType
import org.rsmod.plugins.cache.config.enums.EnumTypeBuilder
import org.rsmod.plugins.cache.packer.util.CacheTypeLiteralUtils.AUTO_INCREMENT_INT
import org.rsmod.plugins.cache.packer.util.CacheTypeLiteralUtils.AUTO_SET_BOOL
import org.rsmod.plugins.cache.packer.util.CacheTypeLiteralUtils.TYPE_STRING_CONVERSION
import org.rsmod.plugins.cache.packer.util.CacheTypeLiteralUtils.convert
import org.rsmod.plugins.cache.packer.util.CacheTypeLiteralUtils.toConvertedEntryMap
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
        val default = default?.convert(names, valId)
        val entries = (entries ?: mutableMapOf())
            .assertAutoElements(entries, values, keyType, valType)
            .putAutoElementEntries(values, keyType, valType, default)
            .toConvertedEntryMap(names, keyId, valId)
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

        fun MutableMap<Any, Any>.putAutoElementEntries(
            values: List<Any>?,
            keyType: String,
            valType: String,
            default: Any?
        ): Map<Any, Any> {
            if (values.isNullOrEmpty()) return this
            val incrementKeys = arrayOfNulls<Any>(values.size)
            val incrementValues = arrayOfNulls<Any>(values.size)
            for (i in values.indices) {
                val key = keyType.autoElementValue(i, values[i], default)
                val value = valType.autoElementValue(i, values[i], default)
                incrementKeys[i] = key
                incrementValues[i] = value
            }
            for (i in values.indices) {
                val key = incrementKeys[i] ?: error("Key cannot be null.")
                val value = incrementValues[i] ?: error("Value cannot be null.")
                this[key] = value
            }
            return this
        }

        private fun MutableMap<Any, Any>.assertAutoElements(
            entries: MutableMap<Any, Any>?,
            values: List<Any>?,
            keyType: String,
            valType: String
        ): MutableMap<Any, Any> {
            /* _could_ add support by adding another field `default_bool` - but adds inconsistency */
            if (keyType == AUTO_SET_BOOL) {
                error("`$AUTO_SET_BOOL` not supported for keys.")
            }
            if (keyType.isAuto || valType.isAuto) {
                if (entries.isNullOrEmpty()) return this
                error(
                    "Cannot define `entries` with auto-key or value types. " +
                        "Use `values` instead."
                )
            }
            if (values.isNullOrEmpty()) return this
            error(
                "Cannot define `values` with non auto-key or value types. " +
                    "Use `entries` instead."
            )
        }

        private fun String.autoElementValue(index: Int, value: Any, default: Any?): Any = when (this) {
            AUTO_SET_BOOL -> default.toAutoBool()
            AUTO_INCREMENT_INT -> index
            else -> value
        }

        private fun Any?.toAutoBool(): Boolean {
            require(this is Boolean) { "`default` value must be boolean when using `$AUTO_SET_BOOL` type." }
            return !this
        }

        private val String.isAuto: Boolean
            get() = this == AUTO_INCREMENT_INT || this == AUTO_SET_BOOL
    }
}
