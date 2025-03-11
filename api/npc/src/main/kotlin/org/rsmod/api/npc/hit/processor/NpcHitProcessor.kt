package org.rsmod.api.npc.hit.processor

import org.rsmod.api.npc.access.StandardNpcAccess
import org.rsmod.game.hit.Hit

public fun interface NpcHitProcessor {
    public fun StandardNpcAccess.process(hit: Hit)
}
