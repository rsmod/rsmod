package org.rsmod.api.config.builders

import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.modlevels
import org.rsmod.api.type.builders.mod.ModLevelBuilder

internal object ModLevelBuilds : ModLevelBuilder() {
    init {
        build("player") { clientCode = constants.mod_clientcode_player }
        build("moderator") { clientCode = constants.mod_clientcode_pmod }

        build("admin") {
            clientCode = constants.mod_clientcode_jmod
            permissions { this += modlevels.moderator }
        }

        build("owner") {
            clientCode = constants.mod_clientcode_jmod
            permissions {
                this += modlevels.moderator
                this += modlevels.admin
            }
        }
    }
}
