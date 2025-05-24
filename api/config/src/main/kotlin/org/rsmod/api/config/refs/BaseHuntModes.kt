package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.hunt.HuntModeReferences

typealias huntmodes = BaseHuntModes

object BaseHuntModes : HuntModeReferences() {
    val aggressive_melee = find("aggressive_melee")
}
