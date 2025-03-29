package org.rsmod.api.spells.runes.combo

import org.rsmod.api.spells.runes.combo.configs.combo_enums
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.util.EnumTypeMapResolver

public class ComboRuneRepository {
    private lateinit var combos: Map<Int, Set<ObjType>>

    public operator fun get(rune: ObjType): Set<ObjType> = combos[rune.id] ?: emptySet()

    internal fun init(resolver: EnumTypeMapResolver) {
        val combos = loadComboRunes(resolver)
        this.combos = combos
    }

    private fun loadComboRunes(resolver: EnumTypeMapResolver): Map<Int, Set<ObjType>> {
        val mapped = mutableMapOf<Int, Set<ObjType>>()
        val comboRuneList = resolver[combo_enums.combos].filterValuesNotNull()
        for ((rune, comboRunesEnum) in comboRuneList) {
            val comboRunes = resolver[comboRunesEnum].filterValuesNotNull()
            mapped[rune.id] = comboRunes.map { it.value }.toHashSet()
        }
        return mapped
    }
}
