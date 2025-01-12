package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.mod.ModGroupBuilder
import org.rsmod.game.type.mod.ModGroup

public object BaseModGroups : ModGroupBuilder() {
    public val player: ModGroup = build("player") { modLevel = BaseModLevels.player }

    public val moderator: ModGroup =
        build("moderator") {
            clientModerator = true
            modLevels = BaseModLevels.player + BaseModLevels.moderator
        }

    public val admin: ModGroup =
        build("admin") {
            clientAdministrator = true
            modLevels = BaseModLevels.player + BaseModLevels.moderator + BaseModLevels.admin
        }

    public val owner: ModGroup =
        build("owner") {
            clientAdministrator = true
            modLevels =
                BaseModLevels.player +
                    BaseModLevels.moderator +
                    BaseModLevels.admin +
                    BaseModLevels.owner
        }
}
