package gg.rsmod.plugins.protocol.codec.game

import com.github.michaelbull.logging.InlineLogger
import gg.rsmod.game.action.ActionHandler
import gg.rsmod.game.model.client.Client
import gg.rsmod.plugins.protocol.codec.account.AccountDispatcher
import gg.rsmod.plugins.protocol.codec.exceptionCaught
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
        if (msg !is ActionHandler<*>) {
            logger.error { "Invalid message type (message=$msg)" }
            return
        }
        while (client.pendingHandlers.size > MAX_ACTION_HANDLERS_PER) {
            client.pendingHandlers.removeAt(0)
        }
        client.pendingHandlers.add(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.exceptionCaught(cause)
    }

    companion object {
        private const val MAX_ACTION_HANDLERS_PER = 25
    }
}
