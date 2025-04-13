package org.rsmod.api.player.events

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.KeyedEvent
import org.rsmod.events.SuspendEvent
import org.rsmod.game.entity.Player

public class PlayerQueueEvents {
    public class Soft<T>(public val player: Player, public val args: T, queueType: Int) :
        KeyedEvent {
        override val id: Long = queueType.toLong()
    }

    public class Protected<T>(public val args: T, queueType: Int) : SuspendEvent<ProtectedAccess> {
        override val id: Long = queueType.toLong()
    }

    public class EngineLabelled(packedLabel: Long) : SuspendEvent<ProtectedAccess> {
        override val id: Long = packedLabel
    }

    public class EngineDefault(queueType: Int) : SuspendEvent<ProtectedAccess> {
        override val id: Long = queueType.toLong()
    }
}
