package org.rsmod.api.spells.runes.staves

import org.rsmod.api.spells.runes.staves.configs.staff_enums
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.util.EnumTypeMapResolver

public class StaffSubstituteRepository {
    private lateinit var subs: Map<Int, Set<Int>>

    public fun isValidSubstitute(baseStaff: ObjType, otherStaff: InvObj): Boolean {
        val substitutes = subs[baseStaff.id] ?: return false
        return otherStaff.id in substitutes
    }

    internal fun init(resolver: EnumTypeMapResolver) {
        val subs = loadStaffSubstitutes(resolver)
        this.subs = subs
    }

    private fun loadStaffSubstitutes(resolver: EnumTypeMapResolver): Map<Int, Set<Int>> {
        val mapped = hashMapOf<Int, Set<Int>>()

        val staffList = resolver[staff_enums.staves].filterValuesNotNull()
        for ((staff, subEnum) in staffList) {
            val subList = resolver[subEnum].filterValuesNotNull()
            mapped[staff.id] = subList.map { it.value.id }.toHashSet()
        }

        return mapped
    }
}
