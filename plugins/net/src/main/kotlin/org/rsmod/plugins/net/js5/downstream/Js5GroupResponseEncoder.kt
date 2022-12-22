package org.rsmod.plugins.net.js5.downstream

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import kotlin.math.min

@Sharable
object Js5GroupResponseEncoder : MessageToByteEncoder<Js5GroupResponse>(Js5GroupResponse::class.java) {

    override fun encode(ctx: ChannelHandlerContext, msg: Js5GroupResponse, out: ByteBuf) {
        var compression = msg.data.readUnsignedByte().toInt()
        if (!msg.urgent) compression = compression or 0x80
        out.writeByte(msg.archive)
        out.writeShort(msg.group)
        out.writeByte(compression)
        out.writeBytes(msg.data, min(msg.data.readableBytes(), 508))
        while (msg.data.isReadable) {
            out.writeByte(0xFF)
            out.writeBytes(msg.data, min(msg.data.readableBytes(), 511))
        }
    }
}
