package org.rsmod.plugins.api.event

import io.netty.channel.Channel
import org.rsmod.game.events.Event
import org.rsmod.game.model.mob.Player

sealed class PlayerSession : Event {

    data class Connected(val channel: Channel, val player: Player) : PlayerSession()
}
