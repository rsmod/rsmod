package gg.rsmod.plugins.protocol.codec.js5

import com.github.michaelbull.logging.InlineLogger
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

private val logger = InlineLogger()

@ChannelHandler.Sharable
object Js5Encoder : MessageToByteEncoder<Js5Response>() {

    private const val BLOCK_LENGTH = 512

    override fun encode(ctx: ChannelHandlerContext, msg: Js5Response, out: ByteBuf) {
        logger.trace { "Encode JS5 response (message=$msg, channel=${ctx.channel()})" }
        out.writeByte(msg.archive)
        out.writeShort(msg.group)
        out.writeByte(msg.compressionType)
        out.writeInt(msg.compressedLength)
        msg.data.forEach { byte ->
            if (out.writerIndex() % BLOCK_LENGTH == 0) {
                /* Notify the client that we're done writing a single block */
                out.writeByte(-1)
            }
            out.writeByte(byte.toInt())
        }
    }
}
