package org.rsmod.plugins.net.js5.downstream

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder

public class XorEncoder : MessageToMessageEncoder<ByteBuf>(ByteBuf::class.java) {

    public var key: Int = 0

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        out += msg.xor(key)
    }
}
