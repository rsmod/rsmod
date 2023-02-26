package org.rsmod.plugins.api.config.type

import org.rsmod.plugins.api.cache.type.enums.EnumType
import org.rsmod.plugins.api.cache.type.enums.EnumTypeBuilder
import org.rsmod.plugins.api.cache.type.literal.CacheTypeIdentifier
import org.rsmod.plugins.api.cache.type.enums.EnumTypeList
import org.rsmod.plugins.api.cache.type.literal.CacheTypeBaseInt
import org.rsmod.plugins.api.cache.type.literal.CacheTypeBaseString
import org.rsmod.plugins.api.config.StringUtil.stripTag
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
        assertAutoIncrement()
        val builder = EnumTypeBuilder()
        val keyId = TYPE_STRING_CONVERSION.getValue(keyType)
        val valId = TYPE_STRING_CONVERSION.getValue(valType)
        val entries = (entries ?: mutableMapOf())
            .takeAutoIncrementValues(values)
            .convertEntries(names, keyId, valId)
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

    private fun MutableMap<Any, Any>.takeAutoIncrementValues(
        values: List<Any>?
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

    private fun assertAutoIncrement() {
        if (keyType == AUTO_INCREMENT_INT || valType == AUTO_INCREMENT_INT) {
            if (entries == null || entries?.isEmpty() == true) return
            error(
                "Cannot define `entries` with auto-increment key or value types. " +
                    "Use `values` instead."
            )
        }
        if (values.isNullOrEmpty()) return
        error(
            "Cannot define `values` with non auto-increment key or value types. " +
                "Use `entries` instead."
        )
    }

    private companion object {

        private const val AUTO_INCREMENT_INT = "autoint"

        private val TYPE_STRING_CONVERSION = mapOf(
            AUTO_INCREMENT_INT to CacheTypeIdentifier.Integer,
            "anim" to CacheTypeIdentifier.Animation,
            "area" to CacheTypeIdentifier.Area,
            "bool" to CacheTypeIdentifier.Boolean,
            "category" to CacheTypeIdentifier.Category,
            "char" to CacheTypeIdentifier.Character,
            "chatchar" to CacheTypeIdentifier.ChatChar,
            "color" to CacheTypeIdentifier.Color,
            "component" to CacheTypeIdentifier.Component,
            "coord" to CacheTypeIdentifier.Coordinate,
            "enum" to CacheTypeIdentifier.Enum,
            "font" to CacheTypeIdentifier.FontMetrics,
            "graphic" to CacheTypeIdentifier.Graphic,
            "identikit" to CacheTypeIdentifier.Idk,
            "int" to CacheTypeIdentifier.Integer,
            "inv" to CacheTypeIdentifier.Inv,
            "item" to CacheTypeIdentifier.Item,
            "maparea" to CacheTypeIdentifier.MapArea,
            "model" to CacheTypeIdentifier.Model,
            "nameditem" to CacheTypeIdentifier.NamedItem,
            "npc" to CacheTypeIdentifier.Npc,
            "object" to CacheTypeIdentifier.Object,
            "stat" to CacheTypeIdentifier.Stat,
            "string" to CacheTypeIdentifier.String,
            "struct" to CacheTypeIdentifier.Struct
        )

        private fun Map<Any, Any>.convertEntries(
            names: NamedTypeMapHolder,
            keyId: CacheTypeIdentifier,
            valId: CacheTypeIdentifier
        ): Map<Any, Any> {
            val converted = mutableMapOf<Any, Any>()
            forEach { (key, value) ->
                val convertedKey = key.convert(names, keyId)
                val convertedValue = value.convert(names, valId)
                converted[convertedKey] = convertedValue
            }
            return converted
        }

        private fun Any.convert(names: NamedTypeMapHolder, id: CacheTypeIdentifier): Any {
            if (id.isString && this is String) return this
            val relative = id.relativeNames(names) ?: return this
            if (this !is String) return this
            val name = this.stripTag()
            return relative[name] ?: error("`$this` could not be found in `${id.out.simpleName}` cache type names.")
        }

        private fun CacheTypeIdentifier.relativeNames(names: NamedTypeMapHolder): Map<String, Any>? = when (this) {
            CacheTypeIdentifier.Component -> names.components
            CacheTypeIdentifier.NamedItem, CacheTypeIdentifier.Item -> names.items
            CacheTypeIdentifier.Npc -> names.npcs
            CacheTypeIdentifier.Object -> names.objs
            CacheTypeIdentifier.Animation -> names.anims
            CacheTypeIdentifier.Graphic -> names.graphics
            CacheTypeIdentifier.Enum -> names.enums
            CacheTypeIdentifier.Struct -> names.structs
            CacheTypeIdentifier.Inv -> names.inventories
            else -> null
        }
    }
}
