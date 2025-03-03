package org.rsmod.api.player.hit.processor

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.hit.Hit

public fun interface QueuedPlayerHitProcessor {
    public fun ProtectedAccess.process(hit: Hit)
}
