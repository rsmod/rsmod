package org.runerealm.api.npc.hit.modifier

import org.runerealm.game.entity.Npc
import org.runerealm.game.hit.HitBuilder

public fun interface NpcHitModifier {
    public fun HitBuilder.modify(target: Npc)
}
