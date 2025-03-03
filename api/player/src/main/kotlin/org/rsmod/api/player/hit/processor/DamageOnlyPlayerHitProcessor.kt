package org.rsmod.api.player.hit.processor

import org.rsmod.game.entity.Player
import org.rsmod.game.hit.Hit

public object DamageOnlyPlayerHitProcessor : InstantPlayerHitProcessor {
    override fun Player.process(hit: Hit) {
        // TODO: Reduce target health.
        // TODO: Show health bar.
        showHitmark(hit.hitmark)
    }
}
