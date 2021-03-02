package org.rsmod.game.net.channel

import io.netty.channel.ChannelHandler

class ClientChannelHandler<T : ChannelHandler>(
    val provider: () -> T,
    val name: String
)
