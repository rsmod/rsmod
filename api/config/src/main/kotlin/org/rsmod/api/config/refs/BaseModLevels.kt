package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.mod.ModLevelReferences
import org.rsmod.game.type.mod.ModLevel

public typealias modlevels = BaseModLevels

public object BaseModLevels : ModLevelReferences() {
    public val player: ModLevel = find("player")
    public val moderator: ModLevel = find("moderator")
    public val admin: ModLevel = find("admin")
    public val owner: ModLevel = find("owner")
}
