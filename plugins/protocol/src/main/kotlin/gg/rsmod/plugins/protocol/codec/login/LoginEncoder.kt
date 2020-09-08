package gg.rsmod.plugins.protocol.codec.login

import com.github.michaelbull.logging.InlineLogger
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

private val logger = InlineLogger()

@ChannelHandler.Sharable
object LoginEncoder : MessageToByteEncoder<LoginResponse>() {

    override fun encode(
        ctx: ChannelHandlerContext,
        msg: LoginResponse,
        out: ByteBuf
    ) {
        logger.debug { "Encode login response (response=$msg, channel=${ctx.channel()})" }
        out.writeResponse(msg)
    }

    private fun ByteBuf.writeResponse(response: LoginResponse) {
        writeByte(2)
        writeByte(13)
        writeByte(0)
        writeInt(0)
        writeByte(response.privilege)
        writeBoolean(true)
        writeShort(response.playerIndex)
        writeBoolean(true)
    }
}
