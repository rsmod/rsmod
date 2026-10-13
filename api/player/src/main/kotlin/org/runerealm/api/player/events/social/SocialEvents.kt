package org.runerealm.api.player.events.social

import org.runerealm.events.UnboundEvent
import org.runerealm.game.entity.Player

/** Joining (with a channel owner's name) or leaving (null name) a friends chat channel. */
public data class FriendChatJoinLeave(val player: Player, val name: String?) : UnboundEvent

/** A friends chat message: public chat typed with a leading "/". */
public data class FriendChatMessage(val player: Player, val text: String) : UnboundEvent

/** A request to kick [name] from the friends chat channel [player] is in. */
public data class FriendChatKick(val player: Player, val name: String) : UnboundEvent

/** The channel owner [player] giving the friend [name] a friends chat [rank]. */
public data class FriendChatSetRank(val player: Player, val name: String, val rank: Int) :
    UnboundEvent

/**
 * A clan chat message: public chat typed with a leading "//" (the player's own clan, [guest] false)
 * or "///" (the clan the player is a guest in, [guest] true).
 */
public data class ClanChatMessage(val player: Player, val text: String, val guest: Boolean) :
    UnboundEvent

/** Friend and ignore list requests sent by the client. */
public sealed class SocialEvents : UnboundEvent {
    public abstract val player: Player
    public abstract val name: String

    public data class FriendAdd(override val player: Player, override val name: String) :
        SocialEvents()

    public data class FriendDel(override val player: Player, override val name: String) :
        SocialEvents()

    public data class IgnoreAdd(override val player: Player, override val name: String) :
        SocialEvents()

    public data class IgnoreDel(override val player: Player, override val name: String) :
        SocialEvents()
}
