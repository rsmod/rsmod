package org.rsmod.api.config.builders

import org.rsmod.api.type.builders.varn.VarnBuilder

internal object VarnBuilds : VarnBuilder() {
    init {
        build("lastcombat")
        build("aggressive_player")
        build("generic_state_2")
        build("attacking_player")
        build("lastattack")
        build("flat_armour")
    }
}
