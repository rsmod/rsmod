package gg.rsmod.plugins.api.protocol.codec.game

import com.github.michaelbull.logging.InlineLogger
import gg.rsmod.game.message.ClientPacketMessage
import gg.rsmod.game.model.client.Client
import gg.rsmod.plugins.api.protocol.codec.account.AccountDispatcher
import gg.rsmod.plugins.api.protocol.codec.exceptionCaught
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

private val logger = InlineLogger()

class GameSessionHandler(
    private val client: Client,
    private val dispatcher: AccountDispatcher
) : ChannelInboundHandlerAdapter() {

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        logger.trace { "Channel registered (username=${client.player.username}, channel=${ctx.channel()})" }
        super.handlerAdded(ctx)
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext) {
        logger.trace { "Channel unregistered (username=${client.player.username}, channel=${ctx.channel()})" }
        dispatcher.unregister(client)
        super.channelUnregistered(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is ClientPacketMessage<*>) {
            logger.error { "Invalid message type (message=$msg)" }
            return
        }
        while (client.pendingPackets.size > PENDING_ACTION_CAPACITY) {
            client.pendingPackets.poll()
        }
        client.pendingPackets.add(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.exceptionCaught(cause)
    }

    companion object {
        private const val PENDING_ACTION_CAPACITY = 250
    }
}
