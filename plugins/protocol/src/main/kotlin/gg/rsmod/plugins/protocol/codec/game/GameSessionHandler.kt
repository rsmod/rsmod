package gg.rsmod.plugins.protocol.codec.game

import com.github.michaelbull.logging.InlineLogger
import gg.rsmod.game.message.ClientPacket
import gg.rsmod.game.model.client.Client
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

private val logger = InlineLogger()

class GameSessionHandler(
    private val client: Client
) : ChannelInboundHandlerAdapter() {

    override fun channelRegistered(ctx: ChannelHandlerContext) {
        logger.debug { "Channel registered (username=${client.player.username}, channel=${ctx.channel()})" }
        super.channelRegistered(ctx)
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext) {
        logger.debug { "Channel unregistered (username=${client.player.username}, channel=${ctx.channel()})" }
        super.channelUnregistered(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is ClientPacket) {
            logger.error { "Invalid message type (message=$msg)" }
            return
        }
        while (client.incomingMessages.size > MAX_CLIENT_MESSAGES) {
            client.incomingMessages.removeAt(0)
        }
        client.incomingMessages.add(msg)
    }

    companion object {
        private const val MAX_CLIENT_MESSAGES = 25
    }
}
