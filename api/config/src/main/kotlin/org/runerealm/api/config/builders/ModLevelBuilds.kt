package org.runerealm.api.config.builders

import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.modlevels
import org.runerealm.api.type.builders.mod.ModLevelBuilder

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
