package org.runerealm.api.player.events

import org.runerealm.events.KeyedEvent
import org.runerealm.game.entity.Player
import org.runerealm.game.type.walktrig.WalkTriggerType

public class PlayerMovementEvent {
    public class WalkTrigger(
        public val player: Player,
        triggerType: WalkTriggerType,
        override val id: Long = triggerType.id.toLong(),
    ) : KeyedEvent
}
