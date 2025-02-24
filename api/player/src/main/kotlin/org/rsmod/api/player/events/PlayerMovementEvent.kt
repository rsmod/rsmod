package org.rsmod.api.player.events

import org.rsmod.events.KeyedEvent
import org.rsmod.game.entity.Player
import org.rsmod.game.type.walktrig.WalkTriggerType

public class PlayerMovementEvent {
    public class WalkTrigger(
        public val player: Player,
        triggerType: WalkTriggerType,
        override val id: Long = triggerType.id.toLong(),
    ) : KeyedEvent
}
