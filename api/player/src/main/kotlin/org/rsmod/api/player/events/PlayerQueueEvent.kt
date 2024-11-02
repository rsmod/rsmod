package org.rsmod.api.player.events

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.KeyedEvent
import org.rsmod.events.SuspendEvent
import org.rsmod.game.entity.Player

public class PlayerQueueEvent {
    public class Soft(public val player: Player, queueType: Int) : KeyedEvent {
        override val id: Long = queueType.toLong()
    }

    public class Protected(queueType: Int) : SuspendEvent<ProtectedAccess> {
        override val id: Long = queueType.toLong()
    }
}
