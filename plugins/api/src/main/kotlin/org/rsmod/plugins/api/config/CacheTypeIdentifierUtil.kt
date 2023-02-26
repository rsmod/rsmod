package org.rsmod.plugins.api.config

import org.rsmod.plugins.api.cache.type.literal.CacheTypeIdentifier
import org.rsmod.plugins.api.config.StringUtil.stripTag
import org.rsmod.plugins.types.NamedTypeMapHolder

internal object CacheTypeIdentifierUtil {

    const val AUTO_INCREMENT_INT = "autoint"

    val TYPE_STRING_CONVERSION = mapOf(
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

    fun Map<Any, Any>.toConvertedEntryMap(
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

    fun Any.convert(names: NamedTypeMapHolder, id: CacheTypeIdentifier): Any {
        if (id.isString && this is String) return this
        val relative = id.relativeNames(names) ?: return this
        if (this !is String) return this
        val name = this.stripTag()
        return relative[name] ?: error("`$this` could not be found in `${id.out.simpleName}` cache type names.")
    }

    fun CacheTypeIdentifier.relativeNames(names: NamedTypeMapHolder): Map<String, Any>? = when (this) {
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
