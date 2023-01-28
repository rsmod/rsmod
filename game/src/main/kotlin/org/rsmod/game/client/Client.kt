package org.rsmod.game.client

import io.netty.channel.Channel
import org.rsmod.game.model.mob.Player

public data class Client(
    public val player: Player,
    public val channel: Channel
)
