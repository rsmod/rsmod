package org.runerealm.api.player.hit.modifier

import org.runerealm.game.entity.Player
import org.runerealm.game.hit.HitBuilder

public fun interface PlayerHitModifier {
    public fun HitBuilder.modify(target: Player)
}
