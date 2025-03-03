package org.rsmod.api.player.hit.processor

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.hit.Hit

public object StandardPlayerHitProcessor : QueuedPlayerHitProcessor {
    override fun ProtectedAccess.process(hit: Hit) {
        // TODO: Process degradation, ring of recoil, retribution, hero points, etc.
        // TODO: Reduce target health.
        // TODO: Show health bar.
        player.showHitmark(hit.hitmark)
    }
}
