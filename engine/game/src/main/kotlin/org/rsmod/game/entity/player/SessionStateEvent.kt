package org.rsmod.game.entity.player

import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Player

public sealed class SessionStateEvent : UnboundEvent {
    public data class Initialize(val player: Player) : SessionStateEvent()

    public data class Login(val player: Player) : SessionStateEvent()

    public data class Logout(val player: Player) : SessionStateEvent()
}
