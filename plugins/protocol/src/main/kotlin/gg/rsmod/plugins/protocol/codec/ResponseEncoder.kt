package gg.rsmod.plugins.protocol.codec

import com.github.michaelbull.logging.InlineLogger
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

private val logger = InlineLogger()

fun Channel.writeErrResponse(err: ResponseType) {
    writeAndFlush(err)
        .addListener(ChannelFutureListener.CLOSE)
}

fun Channel.writeAcceptedResponse() {
    writeAndFlush(ResponseType.ACCEPTED)
}

enum class ResponseType(val id: Int) {
    ACCEPTED(id = 0),
    ERROR_CONNECTING(id = -2),
    BAD_CREDENTIALS(id = 3),
    JS5_OUT_OF_DATE(id = 6),
    WORLD_FULL(id = 9),
    BAD_SESSION_ID(id = 10),
    COULD_NOT_COMPLETE_LOGIN(id = 13),
    TOO_MANY_ATTEMPTS(id = 16),
    ACCOUNT_LOCKED(id = 18),
    MACHINE_INFO_HEADER(id = 68);
}

@ChannelHandler.Sharable
object ResponseEncoder : MessageToByteEncoder<ResponseType>() {

    override fun encode(ctx: ChannelHandlerContext, msg: ResponseType, out: ByteBuf) {
        logger.debug { "Encode response (type=$msg, channel=${ctx.channel()})" }
        out.writeByte(msg.id)
    }
}
