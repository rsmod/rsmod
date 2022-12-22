package org.rsmod.game.net

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import javax.inject.Inject
import javax.inject.Singleton

private typealias InitChannelHandler = (SocketChannel) -> Unit

@Singleton
public class NetworkChannelInitializer(
    private val listeners: MutableList<InitChannelHandler>
) : ChannelInitializer<SocketChannel>() {

    @Inject
    public constructor() : this(mutableListOf())

    override fun initChannel(ch: SocketChannel) {
        listeners.forEach { listener ->
            listener.invoke(ch)
        }
    }

    public fun addListener(channel: (SocketChannel) -> Unit) {
        listeners += channel
    }
}
