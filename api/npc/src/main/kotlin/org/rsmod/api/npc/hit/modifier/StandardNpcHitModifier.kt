package org.rsmod.api.npc.hit.modifier

import jakarta.inject.Inject
import org.rsmod.api.npc.events.NpcHitEvents
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Npc
import org.rsmod.game.hit.HitBuilder

public class StandardNpcHitModifier @Inject constructor(private val eventBus: EventBus) :
    NpcHitModifier {
    override fun HitBuilder.modify(target: Npc) {
        target.publishEvent(this)
        // TODO(combat): Flat armour reduction.
    }

    private fun Npc.publishEvent(hit: HitBuilder) {
        val event = NpcHitEvents.Modify(this, hit)
        eventBus.publish(event)
    }
}
