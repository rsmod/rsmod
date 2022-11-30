package org.rsmod.game.net

import com.google.inject.Inject
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

private typealias InitChannelHandler = (SocketChannel) -> Unit

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
