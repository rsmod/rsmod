package org.rsmod.api.spells.runes.combo

import org.rsmod.api.spells.runes.combo.configs.combo_enums
import org.rsmod.game.enums.EnumTypeMapResolver
import org.rsmod.game.type.obj.ObjType

public class ComboRuneRepository {
    private lateinit var combos: Map<Int, ComboRune>
    public lateinit var comboRunes: List<ComboRune>
        private set

    public operator fun get(comboRune: ObjType): ComboRune? = combos[comboRune.id]

    internal fun init(combos: Map<ObjType, ComboRune>) {
        this.combos = combos.entries.associate { it.key.id to it.value }
        this.comboRunes = combos.values.toList()
    }

    internal fun init(resolver: EnumTypeMapResolver) {
        val combos = loadComboRunes(resolver)
        init(combos)
    }

    private fun loadComboRunes(resolver: EnumTypeMapResolver): Map<ObjType, ComboRune> {
        val mapped = hashMapOf<ObjType, ComboRune>()
        val comboRuneList = resolver[combo_enums.combos].filterValuesNotNull()
        for ((rune, comboRunesEnum) in comboRuneList) {
            val runeList = resolver[comboRunesEnum].filterValuesNotNull().values.toList()
            check(runeList.size == 2) { "Expected 2 rune values: $runeList (enum=$comboRunesEnum)" }
            mapped[rune] = ComboRune(rune, runeList[0], runeList[1])
        }
        return mapped
    }
}
