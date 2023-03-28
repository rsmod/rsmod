package org.rsmod.plugins.cache.literal

import org.rsmod.plugins.cache.literal.codec.CacheTypeBaseInt
import org.rsmod.plugins.cache.literal.codec.CacheTypeBaseString
import org.rsmod.plugins.cache.literal.codec.CacheTypeBoolean
import org.rsmod.plugins.cache.literal.codec.CacheTypeCodec
import org.rsmod.plugins.cache.literal.codec.CacheTypeCoordinate
import org.rsmod.plugins.cache.literal.codec.CacheTypeInt
import org.rsmod.plugins.cache.literal.codec.CacheTypeNamedAnimation
import org.rsmod.plugins.cache.literal.codec.CacheTypeNamedComponent
import org.rsmod.plugins.cache.literal.codec.CacheTypeNamedGraphic
import org.rsmod.plugins.cache.literal.codec.CacheTypeNamedItem
import org.rsmod.plugins.cache.literal.codec.CacheTypeNamedNpc
import org.rsmod.plugins.cache.literal.codec.CacheTypeNamedObject
import org.rsmod.plugins.cache.literal.codec.CacheTypeString
import kotlin.reflect.KClass

public enum class CacheTypeLiteral(
    public val char: Char,
    public val codec: CacheTypeCodec<*, *> = CacheTypeInt
) {
    Animation('A', CacheTypeNamedAnimation),
    Area('R'),
    Boolean('1', CacheTypeBoolean),
    Category('y'),
    Character('z'),
    ChatChar('k'),
    Color('C'),
    Component('I', CacheTypeNamedComponent),
    Coordinate('c', CacheTypeCoordinate),
    DbRow('Ð'),
    EntityOverlay('-'),
    Enum('g'),
    FontMetrics('f'),
    Graphic('d', CacheTypeNamedGraphic),
    Hitmark('×'),
    Idk('K'),
    Integer('i'),
    Interface('a'),
    Inv('v'),

    // meant to be integer-representations of the item, but we only do typed items around here!
    Item('o', CacheTypeNamedItem),
    Jingle('j'),
    MapArea('`'),
    MapElement('µ'),
    Midi('M'),
    Model('m'),
    MoveSpeed('Ý'),
    NamedItem('O', CacheTypeNamedItem),
    Npc('n', CacheTypeNamedNpc),
    NpcMode('N'),
    NpcStat('T'),
    NpcUID('u'),
    Object('l', CacheTypeNamedObject),
    ObjectShape('H'),
    OverlayInterface('L'),
    PlayerUID('p'),
    Spotanim('t'),
    Stat('S'),
    String('s', CacheTypeString),
    Struct('J'),
    Synth('P'),
    Texture('x'),
    TopLevelInterface('F');

    public val isString: kotlin.Boolean get() = codec is CacheTypeBaseString
    public val isInt: kotlin.Boolean get() = codec is CacheTypeBaseInt
    public val out: Class<*> get() = codec.out

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

        private val primitiveTypes: Map<KClass<*>, CacheTypeLiteral> = mapOf(
            Int::class to Integer,
            kotlin.Boolean::class to Boolean,
            kotlin.String::class to String,
            Char::class to Character
        )

        public val values: Array<CacheTypeLiteral> = enumValues()

        public val mapped: Map<Char, CacheTypeLiteral> = values.associateBy { it.char }

        public val mappedOutClasses: Map<KClass<*>, CacheTypeLiteral> = HashMap(
            values
                // Filter type literals with int and string codecs.
                .filter { it.codec != CacheTypeInt && it.codec != CacheTypeString }
                // Map with "out" value class type
                .associateBy { it.out.kotlin } + primitiveTypes
        )
    }
}
