package org.runerealm.api.spells.runes.staves

import org.runerealm.api.spells.runes.staves.configs.staff_enums
import org.runerealm.game.enums.EnumTypeMapResolver
import org.runerealm.game.inv.InvObj
import org.runerealm.game.type.obj.ObjType

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
