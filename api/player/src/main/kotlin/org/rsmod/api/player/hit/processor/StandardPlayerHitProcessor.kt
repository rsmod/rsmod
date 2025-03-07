package org.rsmod.api.player.hit.processor

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.game.hit.Hit

public object StandardPlayerHitProcessor : QueuedPlayerHitProcessor {
    override fun ProtectedAccess.process(hit: Hit) {
        // TODO(combat): Process degradation, ring of recoil, retribution, hero points, etc.
        // TODO(combat): Reduce target health.
        // TODO(combat): Show health bar.
        player.showHitmark(hit.hitmark)
    }
}
