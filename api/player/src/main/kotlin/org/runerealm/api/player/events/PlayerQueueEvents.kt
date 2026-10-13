package org.runerealm.api.player.events

import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.events.KeyedEvent
import org.runerealm.events.SuspendEvent
import org.runerealm.game.entity.Player

public class PlayerQueueEvents {
    public class Soft<T>(public val player: Player, public val args: T, queueType: Int) :
        KeyedEvent {
        override val id: Long = queueType.toLong()
    }

    public class Protected<T>(public val args: T, queueType: Int) : SuspendEvent<ProtectedAccess> {
        override val id: Long = queueType.toLong()
    }
}
