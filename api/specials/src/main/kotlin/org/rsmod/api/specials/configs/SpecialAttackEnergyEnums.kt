package org.rsmod.api.specials.configs

import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.obj.ObjType

internal typealias energy_enums = SpecialAttackEnergyEnums

internal object SpecialAttackEnergyEnums : EnumReferences() {
    val energy_requirements = find<ObjType, Int>("sa_energy_requirements")
    val descriptions = find<ObjType, String>("sa_descriptions")
}
