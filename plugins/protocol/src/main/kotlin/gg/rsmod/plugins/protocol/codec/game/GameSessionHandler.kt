package gg.rsmod.plugins.protocol.codec.game

import com.github.michaelbull.logging.InlineLogger
import gg.rsmod.game.action.ActionMessage
import gg.rsmod.game.model.client.Client
import gg.rsmod.game.model.client.ClientList
import gg.rsmod.plugins.protocol.codec.account.AccountDispatcher
import gg.rsmod.plugins.protocol.codec.exceptionCaught
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

private val logger = InlineLogger()

class GameSessionHandler(
    private val clientList: ClientList,
    private val client: Client,
    private val dispatcher: AccountDispatcher
) : ChannelInboundHandlerAdapter() {

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        logger.trace { "Channel registered (username=${client.player.username}, channel=${ctx.channel()})" }
        clientList.register(client)
        super.handlerAdded(ctx)
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext) {
        logger.trace { "Channel unregistered (username=${client.player.username}, channel=${ctx.channel()})" }
        clientList.remove(client)
        dispatcher.unregister(client)
        super.channelUnregistered(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is ActionMessage<*>) {
            logger.error { "Invalid message type (message=$msg)" }
            return
        }
        while (client.pendingActions.size > PENDING_ACTION_CAPACITY) {
            client.pendingActions.poll()
        }
        client.pendingActions.add(msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.exceptionCaught(cause)
    }

    companion object {
        private const val PENDING_ACTION_CAPACITY = 250
    }
}
