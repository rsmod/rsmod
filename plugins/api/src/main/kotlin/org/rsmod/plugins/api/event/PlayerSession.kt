package org.rsmod.plugins.api.event

import io.netty.channel.Channel
import org.rsmod.game.events.Event

sealed class PlayerSession : Event {

    data class Connected(val channel: Channel) : PlayerSession()
}
