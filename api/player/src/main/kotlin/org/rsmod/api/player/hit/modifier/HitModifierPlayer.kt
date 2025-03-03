package org.rsmod.api.player.hit.modifier

import org.rsmod.game.entity.Player
import org.rsmod.game.hit.HitBuilder

public fun interface HitModifierPlayer {
    public fun HitBuilder.modify(target: Player)
}
