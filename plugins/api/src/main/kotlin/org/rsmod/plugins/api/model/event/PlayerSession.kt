package org.rsmod.plugins.api.model.event

import org.rsmod.game.events.Event
import org.rsmod.game.model.mob.Player

public sealed class PlayerSession : Event {

    public data class Initialize(val player: Player) : PlayerSession()
    public data class LogIn(val player: Player) : PlayerSession()
}
