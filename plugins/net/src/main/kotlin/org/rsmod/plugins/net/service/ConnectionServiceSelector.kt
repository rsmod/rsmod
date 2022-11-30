package org.rsmod.plugins.net.service

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

public class ConnectionServiceSelector : SimpleChannelInboundHandler<ConnectionService>(ConnectionService::class.java) {

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ConnectionService) {
    }
}
