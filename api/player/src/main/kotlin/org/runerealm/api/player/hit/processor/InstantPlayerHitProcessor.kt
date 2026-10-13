package org.runerealm.api.player.hit.processor

import org.runerealm.game.entity.Player
import org.runerealm.game.hit.Hit

public fun interface InstantPlayerHitProcessor {
    public fun Player.process(hit: Hit)
}
