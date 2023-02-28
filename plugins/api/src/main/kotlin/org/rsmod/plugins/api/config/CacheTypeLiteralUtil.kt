package org.rsmod.plugins.api.config

import org.rsmod.plugins.api.cache.type.literal.CacheTypeLiteral
import org.rsmod.plugins.api.config.StringUtil.stripTag
import org.rsmod.plugins.types.NamedTypeMapHolder

internal object CacheTypeLiteralUtil {

    const val AUTO_INCREMENT_INT: String = "autoint"
    const val AUTO_SET_BOOL: String = "autobool"

    val TYPE_STRING_CONVERSION = mapOf(
        AUTO_SET_BOOL to CacheTypeLiteral.Boolean,
        AUTO_INCREMENT_INT to CacheTypeLiteral.Integer,
        "anim" to CacheTypeLiteral.Animation,
        "area" to CacheTypeLiteral.Area,
        "bool" to CacheTypeLiteral.Boolean,
        "category" to CacheTypeLiteral.Category,
        "char" to CacheTypeLiteral.Character,
        "chatchar" to CacheTypeLiteral.ChatChar,
        "color" to CacheTypeLiteral.Color,
        "component" to CacheTypeLiteral.Component,
        "coord" to CacheTypeLiteral.Coordinate,
        "enum" to CacheTypeLiteral.Enum,
        "font" to CacheTypeLiteral.FontMetrics,
        "graphic" to CacheTypeLiteral.Graphic,
        "identikit" to CacheTypeLiteral.Idk,
        "int" to CacheTypeLiteral.Integer,
        "inv" to CacheTypeLiteral.Inv,
        "item" to CacheTypeLiteral.Item,
        "maparea" to CacheTypeLiteral.MapArea,
        "model" to CacheTypeLiteral.Model,
        "nameditem" to CacheTypeLiteral.NamedItem,
        "npc" to CacheTypeLiteral.Npc,
        "object" to CacheTypeLiteral.Object,
        "stat" to CacheTypeLiteral.Stat,
        "string" to CacheTypeLiteral.String,
        "struct" to CacheTypeLiteral.Struct
    )

    fun Map<Any, Any>.toConvertedEntryMap(
        names: NamedTypeMapHolder,
        keyId: CacheTypeLiteral,
        valId: CacheTypeLiteral
    ): Map<Any, Any> {
        val converted = mutableMapOf<Any, Any>()
        forEach { (key, value) ->
            val convertedKey = key.convert(names, keyId)
            val convertedValue = value.convert(names, valId)
            converted[convertedKey] = convertedValue
        }
        return converted
    }

    fun Any.convert(names: NamedTypeMapHolder, id: CacheTypeLiteral): Any {
        if (id.isString && this is String) return this
        val relative = id.relativeNames(names) ?: return this
        if (this !is String) return this
        val name = this.stripTag()
        return relative[name] ?: error("`$this` could not be found in `${id.out.simpleName}` cache type names.")
    }

    private fun CacheTypeLiteral.relativeNames(names: NamedTypeMapHolder): Map<String, Any>? = when (this) {
        CacheTypeLiteral.Component -> names.components
        CacheTypeLiteral.NamedItem, CacheTypeLiteral.Item -> names.items
        CacheTypeLiteral.Npc -> names.npcs
        CacheTypeLiteral.Object -> names.objs
        CacheTypeLiteral.Animation -> names.anims
        CacheTypeLiteral.Graphic -> names.graphics
        CacheTypeLiteral.Enum -> names.enums
        CacheTypeLiteral.Struct -> names.structs
        CacheTypeLiteral.Inv -> names.inventories
        else -> null
    }
}
