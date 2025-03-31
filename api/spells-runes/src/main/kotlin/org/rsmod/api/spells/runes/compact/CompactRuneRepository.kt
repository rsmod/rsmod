package org.rsmod.api.spells.runes.compact

import org.rsmod.api.spells.runes.compact.configs.compact_enums
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.util.EnumTypeMapResolver

/**
 * Rune objs have a "compact id" used in places where storage needs to be efficient, such as rune
 * pouches. This class is responsible for providing access to these "compact ids."
 */
public class CompactRuneRepository {
    private lateinit var compactIds: Map<Int, Int>
    private lateinit var reverseLookup: Map<Int, ObjType>

    public operator fun get(rune: ObjType): Int? = compactIds[rune.id]

    internal fun init(compactIds: Map<ObjType, Int>) {
        this.compactIds = compactIds.entries.associate { it.key.id to it.value }
        this.reverseLookup = compactIds.entries.associate { it.value to it.key }
    }

    internal fun init(resolver: EnumTypeMapResolver) {
        val compactIds = loadCompactIds(resolver)
        init(compactIds)
    }

    private fun loadCompactIds(resolver: EnumTypeMapResolver): Map<ObjType, Int> {
        val enum = resolver[compact_enums.compact_ids].filterValuesNotNull()
        return HashMap(enum.backing)
    }
}
