package org.rsmod.api.player.hit.modifier

import org.rsmod.game.entity.Player
import org.rsmod.game.hit.HitBuilder

public object NoopPlayerHitModifier : PlayerHitModifier {
    override fun HitBuilder.modify(target: Player) {}
}
