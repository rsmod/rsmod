package org.runerealm.api.player.events

import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.events.KeyedEvent
import org.runerealm.events.SuspendEvent
import org.runerealm.game.entity.Player

public class PlayerTimerEvent {
    public class Normal(timerType: Int) : SuspendEvent<ProtectedAccess> {
        override val id: Long = timerType.toLong()
    }

    public class Soft(public val player: Player, timerType: Int) : KeyedEvent {
        override val id: Long = timerType.toLong()
    }
}
