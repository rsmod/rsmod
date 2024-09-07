package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.mod.ModLevelReferences
import org.rsmod.game.type.mod.ModLevel

public object BaseModLevels : ModLevelReferences() {
    public val player: ModLevel = find("player")
}
