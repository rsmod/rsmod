package org.rsmod.plugins.api.cache.type.literal

import org.rsmod.game.model.map.Coordinates
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeBaseInt
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeBaseString
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeBoolean
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeCodec
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeCoordinate
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeInt
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeNamedAnimation
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeNamedComponent
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeNamedGraphic
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeNamedItem
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeNamedNpc
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeNamedObject
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeString
import org.rsmod.plugins.types.NamedAnimation
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedGraphic
import org.rsmod.plugins.types.NamedNpc
import org.rsmod.plugins.types.NamedObject

public enum class CacheTypeLiteral(
    public val char: Char,
    public val codec: CacheTypeCodec<*, *> = CacheTypeInt,
    public val out: Class<*> = Int::class.java
) {
    Animation('A', CacheTypeNamedAnimation, NamedAnimation::class.java),
    Area('R'),
    Boolean('1', CacheTypeBoolean, kotlin.Boolean::class.java),
    Category('y'),
    Character('z'),
    ChatChar('k'),
    Color('C'),
    Component('I', CacheTypeNamedComponent, NamedComponent::class.java),
    Coordinate('c', CacheTypeCoordinate, Coordinates::class.java),
    DbRow('￐'),
    Enum('g'),
    FontMetrics('f'),
    Graphic('d', CacheTypeNamedGraphic, NamedGraphic::class.java),
    Idk('K'),
    Integer('i'),
    Inv('v'),

    // meant to be integer-representations of the item, but we only do typed items around here!
    Item('o', CacheTypeNamedItem, org.rsmod.plugins.types.NamedItem::class.java),
    MapArea('`'),
    Model('m'),
    NamedItem('O', CacheTypeNamedItem, org.rsmod.plugins.types.NamedItem::class.java),
    Npc('n', CacheTypeNamedNpc, NamedNpc::class.java),
    Object('l', CacheTypeNamedObject, NamedObject::class.java),
    Stat('S'),
    String('s', CacheTypeString, kotlin.String::class.java),
    Struct('J'),
    Unknown1('Ð'),
    Unknown2('P'),
    Unknown3('x'),
    Unknown5('ﾵ');

    public val isString: kotlin.Boolean get() = codec is CacheTypeBaseString
    public val isInt: kotlin.Boolean get() = codec is CacheTypeBaseInt

    @Suppress("UNCHECKED_CAST")
    public fun encodeString(value: Any): kotlin.String {
        check(isString)
        val codec = codec as CacheTypeBaseString<Any>
        return codec.encode(value)
    }

    @Suppress("UNCHECKED_CAST")
    public fun decodeString(value: kotlin.String): Any? {
        check(isString)
        val codec = codec as CacheTypeBaseString<Any>
        return codec.decode(value)
    }

    @Suppress("UNCHECKED_CAST")
    public fun encodeInt(value: Any): Int {
        check(isInt)
        val codec = codec as CacheTypeBaseInt<Any>
        return codec.encode(value)
    }

    @Suppress("UNCHECKED_CAST")
    public fun decodeInt(value: Int): Any? {
        check(isInt)
        val codec = codec as CacheTypeBaseInt<Any>
        return codec.decode(value)
    }

    public companion object {

        public val values: Array<CacheTypeLiteral> = enumValues()

        public val mapped: Map<Char, CacheTypeLiteral> = values.associateBy { it.char }
    }
}
