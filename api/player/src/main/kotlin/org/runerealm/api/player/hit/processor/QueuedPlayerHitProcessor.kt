package org.runerealm.api.player.hit.processor

import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.game.hit.Hit

public fun interface QueuedPlayerHitProcessor {
    public fun ProtectedAccess.process(hit: Hit)
}
