package org.rsmod.game.type.literal

/*
 * These do _not_ follow our normal convention of PascalCase for enum entries. It is simply because
 * they pollute our standard namespace with classes such as [org.rsmod.map.CoordGrid], and even
 * internal references to [kotlin.Char] or [kotlin.Int].
 */
@Suppress("konsist.enum entries require PascalCase")
public enum class CacheVarLiteral(
    public val char: Char,
    public val type: BaseCacheVarType = BaseCacheVarType.Integer,
) {
    BOOL('1'),
    ENTITY_OVERLAY('-'),
    SEQ('A'),
    COLOUR('C'),
    TOPLEVEL_INTERFACE('F'),
    LOC_SHAPE('H'),
    COMPONENT('I'),
    STRUCT('J'),
    IDKIT('K'),
    OVERLAY_INTERFACE('L'),
    MIDI('M'),
    NPCMODE('N'),
    NAMEDOBJ('O'),
    SYNTH('P'),
    AREA('R'),
    STAT('S'),
    NPCSTAT('T'),
    MAPAREA('`'),
    INTERFACE('a'),
    COORDGRID('c'),
    GRAPHIC('d'),
    FONTMETRICS('f'),
    ENUM('g'),
    JINGLE('j'),
    INT('i'),
    LOC('l'),
    MODEL('m'),
    NPC('n'),
    OBJ('o'),
    PLAYERUID('p'),
    STRING('s', type = BaseCacheVarType.String),
    SPOTANIM('t'),
    NPCUID('u'),
    INV('v'),
    TEXTURE('x'),
    CATEGORY('y'),
    CHAR('z'),
    MAPELEMENT('µ'),
    HITMARK('×'),
    STRINGVECTOR('¸'),
    DBTABLE('Ø'),
    DBROW('Ð'),
    MOVESPEED('Ý'),
    VARP('7'),
    VARBIT(']');

    public companion object {
        public fun forCharId(char: Char?): CacheVarLiteral? =
            if (char != null) {
                CacheVarLiteral.entries.firstOrNull { it.char == char }
            } else {
                null
            }
    }
}
