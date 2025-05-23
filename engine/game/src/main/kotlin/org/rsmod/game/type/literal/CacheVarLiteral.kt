package org.rsmod.game.type.literal

/*
 * These do _not_ follow our normal convention of PascalCase for enum entries. It is simply because
 * they pollute our standard namespace with classes such as [org.rsmod.map.CoordGrid], and even
 * internal references to [kotlin.Char] or [kotlin.Int].
 */
@Suppress("konsist.enum entries require PascalCase")
public enum class CacheVarLiteral(
    public val char: Char,
    public val id: Int,
    public val type: BaseCacheVarType = BaseCacheVarType.Integer,
) {
    BOOL('1', id = 1),
    ENTITY_OVERLAY('-', id = 117),
    SEQ('A', id = 6),
    COLOUR('C', id = 7),
    TOPLEVEL_INTERFACE('F', id = 98),
    LOC_SHAPE('H', id = 8),
    COMPONENT('I', id = 9),
    STRUCT('J', id = 73),
    IDKIT('K', id = 10),
    OVERLAY_INTERFACE('L', id = 99),
    MIDI('M', id = 11),
    NPCMODE('N', id = 12),
    NAMEDOBJ('O', id = 13),
    SYNTH('P', id = 14),
    AREA('R', id = 16),
    STAT('S', id = 17),
    NPCSTAT('T', id = 18),
    MAPAREA('`', id = 21),
    INTERFACE('a', id = 97),
    COORDGRID('c', id = 22),
    GRAPHIC('d', id = 23),
    FONTMETRICS('f', id = 25),
    ENUM('g', id = 26),
    JINGLE('j', id = 28),
    INT('i', id = 0),
    LOC('l', id = 30),
    MODEL('m', id = 31),
    NPC('n', id = 32),
    OBJ('o', id = 33),
    PLAYERUID('p', id = 34),
    STRING('s', id = 36, type = BaseCacheVarType.String),
    SPOTANIM('t', id = 37),
    NPCUID('u', id = 38),
    INV('v', id = 39),
    TEXTURE('x', id = 40),
    CATEGORY('y', id = 41),
    CHAR('z', id = 42),
    MAPELEMENT('µ', id = 59),
    HITMARK('×', id = 62),
    HEADBAR('¯', id = 93),
    STRINGVECTOR('¸', id = -1),
    DBTABLE('Ø', id = 118),
    DBROW('Ð', id = 74),
    MOVESPEED('Ý', id = 101),
    VARP('7', id = 209),
    VARBIT(']', id = 254),
    PROJANIM('[', id = 253);

    public companion object {
        public val mappedChars: Map<Char, CacheVarLiteral> = entries.associateBy { it.char }

        public val mappedIds: Map<Int, CacheVarLiteral> = entries.associateBy { it.id }

        public fun forCharId(char: Char?): CacheVarLiteral? = mappedChars[char]

        public operator fun get(id: Int): CacheVarLiteral? = mappedIds[id]
    }
}
