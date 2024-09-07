package org.rsmod.api.player.events

import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Player

public sealed class SessionStateEvent : UnboundEvent {
    public data class Initialize(val player: Player) : SessionStateEvent()

    public data class LogIn(val player: Player) : SessionStateEvent()

    public data class LogOut(val player: Player) : SessionStateEvent()
}
