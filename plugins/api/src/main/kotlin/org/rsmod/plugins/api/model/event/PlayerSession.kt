package org.rsmod.plugins.api.model.event

import org.rsmod.game.events.GameEvent
import org.rsmod.game.model.mob.Player

public sealed class PlayerSession : GameEvent {

    public data class Initialize(val player: Player) : PlayerSession()
    public data class LogIn(val player: Player) : PlayerSession()
    public data class LogOut(val player: Player) : PlayerSession()
    public data class Finalize(val player: Player) : PlayerSession()
}
