package org.rsmod.api.config.refs

import org.rsmod.api.type.builders.mod.ModGroupBuilder
import org.rsmod.game.type.mod.ModGroup

public object BaseModGroups : ModGroupBuilder() {
    public val player: ModGroup =
        build("player") {
            moderator = false // Declared for explicitness in api - not required.
            administrator = false // Declared for explicitness in api - not required.
            modLevels = setOf(BaseModLevels.player)
        }
}
