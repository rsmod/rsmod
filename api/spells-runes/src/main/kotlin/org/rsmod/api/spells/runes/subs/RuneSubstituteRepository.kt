package org.rsmod.api.spells.runes.subs

import org.rsmod.api.spells.runes.subs.configs.runesub_enums
import org.rsmod.game.enums.EnumTypeMapResolver
import org.rsmod.game.type.obj.ObjType

public class RuneSubstituteRepository {
    private lateinit var subs: Map<Int, List<ObjType>>

    public operator fun get(baseRune: ObjType): List<ObjType>? = subs[baseRune.id]

    internal fun init(subs: Map<Int, List<ObjType>>) {
        this.subs = subs
    }

    internal fun init(resolver: EnumTypeMapResolver) {
        val subs = loadRuneSubstitutes(resolver)
        init(subs)
    }

    private fun loadRuneSubstitutes(resolver: EnumTypeMapResolver): Map<Int, List<ObjType>> {
        val mapped = hashMapOf<Int, List<ObjType>>()

        val runeList = resolver[runesub_enums.runes].filterValuesNotNull()
        for ((rune, subEnum) in runeList) {
            val subList = resolver[subEnum].filterValuesNotNull()
            mapped[rune.id] = subList.values.toList()
        }

        return mapped
    }
}
