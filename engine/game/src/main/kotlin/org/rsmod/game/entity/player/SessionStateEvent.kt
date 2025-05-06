package org.rsmod.game.entity.player

import org.rsmod.events.UnboundEvent
import org.rsmod.game.entity.Player

public class SessionStateEvent {
    /** Fired when a player is registered to the player list. */
    public data class Initialize(val player: Player) : UnboundEvent

    /** Fired after [Initialize] during the player login sequence. */
    public data class Login(val player: Player) : UnboundEvent

    /** Fired before the player's account data is queued for saving. */
    public data class Logout(val player: Player) : UnboundEvent

    /** Fired when a player is unregistered from the player list. */
    public data class Delete(val player: Player) : UnboundEvent
}
