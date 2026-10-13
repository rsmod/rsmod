package org.runerealm.api.specials.configs

import org.runerealm.api.type.refs.enums.EnumReferences
import org.runerealm.game.type.obj.ObjType

internal typealias energy_enums = SpecialAttackEnergyEnums

internal object SpecialAttackEnergyEnums : EnumReferences() {
    val energy_requirements = find<ObjType, Int>("sa_energy_requirements")
    val descriptions = find<ObjType, String>("sa_descriptions")
}
