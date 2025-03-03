package org.rsmod.api.player.hit.processor

import org.rsmod.game.entity.Player
import org.rsmod.game.hit.Hit

public fun interface InstantPlayerHitProcessor {
    public fun Player.process(hit: Hit)
}
