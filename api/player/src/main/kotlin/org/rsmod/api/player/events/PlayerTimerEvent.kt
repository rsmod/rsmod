package org.rsmod.api.player.events

import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.events.KeyedEvent
import org.rsmod.events.SuspendEvent
import org.rsmod.game.entity.Player

public class PlayerTimerEvent {
    public class Normal(timerType: Int) : SuspendEvent<ProtectedAccess> {
        override val id: Long = timerType.toLong()
    }

    public class Soft(public val player: Player, timerType: Int) : KeyedEvent {
        override val id: Long = timerType.toLong()
    }
}
