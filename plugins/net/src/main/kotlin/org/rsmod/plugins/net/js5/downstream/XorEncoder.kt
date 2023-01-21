package org.rsmod.plugins.net.js5.downstream

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder

class XorEncoder : MessageToMessageEncoder<ByteBuf>(ByteBuf::class.java) {

    var key = 0

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        out += msg.xor(key)
    }
}
