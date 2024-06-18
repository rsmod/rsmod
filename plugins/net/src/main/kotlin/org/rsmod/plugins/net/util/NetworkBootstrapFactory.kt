package org.rsmod.plugins.net.util

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import com.google.inject.Inject
import com.google.inject.Singleton

@Singleton
public class NetworkBootstrapFactory @Inject constructor(
    private val allocator: ByteBufAllocator
) {

    public fun createBootstrap(group: EventLoopGroup): ServerBootstrap {
        val channel = when (group) {
            is NioEventLoopGroup -> NioServerSocketChannel::class.java
            else -> error("Unhandled EventLoopGroup channel conversion. (${group.javaClass.simpleName})")
        }
        return ServerBootstrap()
            .group(group)
            .channel(channel)
            .option(ChannelOption.ALLOCATOR, allocator)
            .childOption(ChannelOption.ALLOCATOR, allocator)
            .childOption(ChannelOption.AUTO_READ, false)
            .childOption(ChannelOption.TCP_NODELAY, true)
    }

    public fun createEventLoopGroup(): EventLoopGroup {
        return NioEventLoopGroup()
    }
}
