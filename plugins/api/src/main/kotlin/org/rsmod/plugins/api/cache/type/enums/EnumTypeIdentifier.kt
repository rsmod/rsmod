package org.rsmod.plugins.api.cache.type.enums

import org.rsmod.game.model.map.Coordinates
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeBaseInt
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeBaseString
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeBoolean
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeCoordinate
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeInt
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeLiteral
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeNamedAnimation
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeNamedComponent
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeNamedGraphic
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeNamedItem
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeNamedNpc
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeNamedObject
import org.rsmod.plugins.api.cache.type.enums.literal.EnumTypeString
import org.rsmod.plugins.types.NamedAnimation
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedGraphic
import org.rsmod.plugins.types.NamedNpc
import org.rsmod.plugins.types.NamedObject

/**
 * @param char The byte used within the enum type data to
 * determine its input and output type.
 */
public enum class EnumTypeIdentifier(
    public val char: Char,
    public val literal: EnumTypeLiteral<*, *> = EnumTypeInt,
    public val out: Class<*> = Int::class.java
) {
    Animation('A', EnumTypeNamedAnimation, NamedAnimation::class.java),
    Area('R'),
    Boolean('1', EnumTypeBoolean, kotlin.Boolean::class.java),
    Category('y'),
    Character('z'),
    ChatChar('k'),
    Color('C'),
    Component('I', EnumTypeNamedComponent, NamedComponent::class.java),
    Coordinate('c', EnumTypeCoordinate, Coordinates::class.java),
    Enum('g'),
    FontMetrics('f'),
    Graphic('d', EnumTypeNamedGraphic, NamedGraphic::class.java),
    Idk('K'),
    Integer('i'),
    Inv('v'),

    // meant to be integer-representations of the item, but we only do typed items around here!
    Item('o', EnumTypeNamedItem, org.rsmod.plugins.types.NamedItem::class.java),
    MapArea('`'),
    Model('m'),
    NamedItem('O', EnumTypeNamedItem, org.rsmod.plugins.types.NamedItem::class.java),
    Npc('n', EnumTypeNamedNpc, NamedNpc::class.java),
    Object('l', EnumTypeNamedObject, NamedObject::class.java),
    Stat('S'),
    String('s', EnumTypeString, kotlin.String::class.java),
    Struct('J'),
    Unknown1('Ð'),
    Unknown2('P'),
    Unknown3('x'),
    Unknown4('￐'),
    Unknown5('ﾵ');

    public val isString: kotlin.Boolean get() = literal is EnumTypeBaseString
    public val isInt: kotlin.Boolean get() = literal is EnumTypeBaseInt

    public companion object {

        public val values: Array<EnumTypeIdentifier> = enumValues()
    }
}
