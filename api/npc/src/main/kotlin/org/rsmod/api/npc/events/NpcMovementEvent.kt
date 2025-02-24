package org.rsmod.api.npc.events

import org.rsmod.events.KeyedEvent
import org.rsmod.game.entity.Npc
import org.rsmod.game.type.walktrig.WalkTriggerType

public class NpcMovementEvent {
    public class WalkTrigger(
        public val npc: Npc,
        triggerType: WalkTriggerType,
        override val id: Long = triggerType.id.toLong(),
    ) : KeyedEvent
}
