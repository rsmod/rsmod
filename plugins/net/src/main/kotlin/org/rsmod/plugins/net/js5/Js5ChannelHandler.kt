package org.rsmod.plugins.net.js5

import com.github.michaelbull.logging.InlineLogger
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleStateEvent
import jakarta.inject.Inject
import org.rsmod.plugins.net.js5.downstream.XorEncoder
import org.rsmod.plugins.net.js5.upstream.Js5Request

private val logger = InlineLogger()

public class Js5ChannelHandler @Inject constructor(
    private val service: Js5Service
) : SimpleChannelInboundHandler<Js5Request>(Js5Request::class.java) {

    private lateinit var client: Js5Client

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        client = Js5Client(ctx.read())
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Js5Request) {
        when (msg) {
            is Js5Request.Group -> service.push(client, msg)
            is Js5Request.Rekey -> handleMessage(ctx, msg)
            Js5Request.Disconnect -> ctx.close()
            else -> logger.debug { "Unhandled Js5 request: $msg" }
        }
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        service.readIfNotFull(client)
    }

    override fun channelWritabilityChanged(ctx: ChannelHandlerContext) {
        if (ctx.channel().isWritable) {
            service.notifyIfNotEmpty(client)
        }
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {
            ctx.close()
        }
    }

    private fun handleMessage(ctx: ChannelHandlerContext, msg: Js5Request.Rekey) {
        val encoder = ctx.pipeline().get(XorEncoder::class.java)
        encoder.key = msg.key
    }
}
