package org.runerealm.api.npc.events

import org.runerealm.events.KeyedEvent
import org.runerealm.game.entity.Npc
import org.runerealm.game.type.walktrig.WalkTriggerType

public class NpcMovementEvent {
    public class WalkTrigger(
        public val npc: Npc,
        triggerType: WalkTriggerType,
        override val id: Long = triggerType.id.toLong(),
    ) : KeyedEvent
}
