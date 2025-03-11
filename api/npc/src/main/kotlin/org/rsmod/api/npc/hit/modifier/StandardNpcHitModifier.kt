package org.rsmod.api.npc.hit.modifier

import jakarta.inject.Inject
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.hit.HitBuilder

public class StandardNpcHitModifier @Inject constructor(private val eventBus: EventBus) :
    HitModifierNpc {
    override fun HitBuilder.modify(target: Npc) {
        // TODO(combat): Protection prayers based on npc type pre-defined overheads.
    }
}
