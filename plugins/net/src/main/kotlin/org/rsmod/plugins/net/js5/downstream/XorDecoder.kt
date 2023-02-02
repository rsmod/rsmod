package org.rsmod.plugins.net.js5.downstream

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder

public class XorDecoder : MessageToMessageDecoder<ByteBuf>(ByteBuf::class.java) {

    private var key: Int = 0

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        out += msg.xor(key)
    }
}
