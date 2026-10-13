package org.runerealm.api.npc.hit.processor

import org.runerealm.api.npc.access.StandardNpcAccess
import org.runerealm.game.hit.Hit

public fun interface NpcHitProcessor {
    public fun StandardNpcAccess.process(hit: Hit)
}
