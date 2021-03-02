package org.rsmod.game.net.channel

import com.github.michaelbull.logging.InlineLogger
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

private val logger = InlineLogger()

class ClientChannelInitializer(
    private val handshakeDecoder: () -> ChannelHandler
) : ChannelInitializer<SocketChannel>() {

    override fun initChannel(ch: SocketChannel) {
        logger.debug { "Initialize channel (channel=$ch)" }
        ch.pipeline().addLast(handshakeDecoder())
    }
}
