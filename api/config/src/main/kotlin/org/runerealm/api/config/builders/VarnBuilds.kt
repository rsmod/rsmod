package org.runerealm.api.config.builders

import org.runerealm.api.type.builders.varn.VarnBuilder

internal object VarnBuilds : VarnBuilder() {
    init {
        build("lastcombat")
        build("aggressive_player")
        build("generic_state_2")
        build("attacking_player")
        build("lastattack")
        build("flat_armour")
        build("freeze_end")
        build("freeze_immunity_end")
    }
}
