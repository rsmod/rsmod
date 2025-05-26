package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.hunt.HuntModeReferences

typealias huntmodes = BaseHuntModes

object BaseHuntModes : HuntModeReferences() {
    val ranged = find("ranged")
    val constant_melee = find("constant_melee")
    val constant_ranged = find("constant_ranged")
    val cowardly = find("cowardly")
    val notbusy_melee = find("notbusy_melee")
    val notbusy_range = find("notbusy_range")
    val aggressive_melee = find("aggressive_melee")
    val aggressive_melee_extra = find("aggressive_melee_extra")
    val aggressive_ranged = find("aggressive_ranged")
}
