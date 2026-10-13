package org.runerealm.api.player.hit.modifier

import org.runerealm.game.entity.Player
import org.runerealm.game.hit.HitBuilder

public object NoopPlayerHitModifier : PlayerHitModifier {
    override fun HitBuilder.modify(target: Player) {}
}
