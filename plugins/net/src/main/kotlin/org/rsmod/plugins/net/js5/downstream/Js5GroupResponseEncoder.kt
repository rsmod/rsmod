package org.rsmod.plugins.net.js5.downstream

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import kotlin.math.min

@Sharable
object Js5GroupResponseEncoder : MessageToByteEncoder<Js5GroupResponse>(Js5GroupResponse::class.java) {

    override fun encode(ctx: ChannelHandlerContext, msg: Js5GroupResponse, out: ByteBuf) {
        val compression = msg.data.readUnsignedByte().toInt()
        out.writeByte(msg.archive)
        out.writeShort(msg.group)
        out.writeByte(compression)
        out.writeBytes(msg.data, min(msg.data.readableBytes(), 508))
        while (msg.data.isReadable) {
            out.writeByte(0xFF)
            out.writeBytes(msg.data, min(msg.data.readableBytes(), 511))
        }
    }

    override fun allocateBuffer(ctx: ChannelHandlerContext, msg: Js5GroupResponse, preferDirect: Boolean): ByteBuf {
        val dataLen = msg.data.readableBytes()
        /*
         * The naive code to estimate the length is:
         *
         *     var len = 3    // for the archive/group header
         *     len += dataLen // for the data itself
         *
         *     // first 0xFF marker
         *     if (dataLen > 509) { // compression byte plus the first 508 bytes of data
         *         len++
         *         dataLen -= 509
         *
         *         // remaining 0xFF markers
         *         while (dataLen > 511) {
         *             len++
         *             dataLen -= 511
         *         }
         *     }
         *
         * We can simplify this by combining the if and while loop by adding an
         * extra 2 bytes to dataLen, which ensures we add the first marker
         * after 509 bytes (511-2) and then all subsequent markers after 511
         * bytes:
         *
         *     var len = 3
         *     len += dataLen
         *
         *     dataLen += 2
         *
         *     while (dataLen > 511) {
         *         len++
         *         dataLen -= 511
         *     }
         *
         * The while loop can be replaced with division:
         *
         *     var len = 3
         *     len += dataLen
         *     len += divide(dataLen + 2, 511)
         *
         * divide() is almost but not quite standard division. We want
         * divide(x, 511) to produce 0 if x=511, and only produce 1 if x=512.
         * Similarly, it should produce 1 if x=1022, and only produce 1 if
         * x=1023, and so on.
         *
         * This can be achieved by implementing divide(x, y) as
         * ((x + (y - 1)) / y) - 1. So in our case, where y=511,
         * ((x + 510) / y) - 1.
         *
         * Combined with the above, our length calculation becomes:
         *
         *     var len = 3
         *     len += dataLen
         *     len += (dataLen + 2 + 510) / 511 - 1
         *
         * which we can simplify to:
         *
         *     val len = 2 + dataLen + (512 + dataLen) / 511
         *
         * As this is confusing, testAllocateBuffer() has a test to ensure the
         * length calculation is correct for all dataLens between 1 and 1534
         * inclusive, which covers all lengths for 1, 2 and 3 block responses
         * completely, and one length for a 4 block responses (as a boundary
         * check).
         */
        val len = 2 + dataLen + (512 + dataLen) / 511
        return if (preferDirect) {
            ctx.alloc().ioBuffer(len, len)
        } else {
            ctx.alloc().heapBuffer(len, len)
        }
    }
}
