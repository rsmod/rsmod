package org.rsmod.api.npc.hit.modifier

import org.rsmod.game.entity.Npc
import org.rsmod.game.hit.HitBuilder

public object StandardNpcHitModifier : HitModifierNpc {
    override fun HitBuilder.modify(target: Npc) {
        // TODO(combat): Protection prayers based on npc type pre-defined overheads.
    }
}
