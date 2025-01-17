@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.mod.ModGroupBuilder

object BaseModGroups : ModGroupBuilder() {
    val player = build("player") { modLevel = BaseModLevels.player }

    val moderator =
        build("moderator") {
            clientModerator = true
            modLevels = BaseModLevels.player + BaseModLevels.moderator
        }

    val admin =
        build("admin") {
            clientAdministrator = true
            modLevels = BaseModLevels.player + BaseModLevels.moderator + BaseModLevels.admin
        }

    val owner =
        build("owner") {
            clientAdministrator = true
            modLevels =
                BaseModLevels.player +
                    BaseModLevels.moderator +
                    BaseModLevels.admin +
                    BaseModLevels.owner
        }
}
