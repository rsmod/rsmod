package gg.rsmod.plugins.core.protocol.codec.js5

import com.github.michaelbull.logging.InlineLogger
import gg.rsmod.plugins.core.protocol.codec.ResponseType
import gg.rsmod.plugins.core.protocol.codec.writeAcceptedResponse
import gg.rsmod.plugins.core.protocol.codec.writeErrResponse
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

private val logger = InlineLogger()

sealed class Js5Stage {
    object Handshake : Js5Stage()
    object Request : Js5Stage()
}

class Js5Decoder(
    private val revision: Int,
    private var stage: Js5Stage = Js5Stage.Handshake
) : ByteToMessageDecoder() {

    override fun decode(
        ctx: ChannelHandlerContext,
        buf: ByteBuf,
        out: MutableList<Any>
    ) {
        logger.trace { "Decode JS5 message (stage=$stage, channel=${ctx.channel()})" }
        when (stage) {
            Js5Stage.Handshake -> ctx.channel().readHandshake(buf)
            Js5Stage.Request -> ctx.channel().readFileRequest(buf, out)
        }
    }

    private fun Channel.readHandshake(buf: ByteBuf) {
        val clientRevision = buf.readInt()
        if (clientRevision < revision) {
            logger.info {
                "Handshake revision out-of-date " +
                    "(clientMajor=$clientRevision, serverMajor=$revision, channel=$this)"
            }
            writeErrResponse(ResponseType.JS5_OUT_OF_DATE)
            return
        }
        logger.trace { "Handshake accepted (channel=$this)" }
        stage = Js5Stage.Request
        writeAcceptedResponse()
    }

    private fun Channel.readFileRequest(
        buf: ByteBuf,
        out: MutableList<Any>
    ) {
        buf.markReaderIndex()
        when (val opcode = buf.readByte().toInt()) {
            NORMAL_FILE_REQUEST -> buf.readFileRequest(out, urgent = false)
            URGENT_FILE_REQUEST -> buf.readFileRequest(out, urgent = true)
            CLIENT_INIT_GAME,
            CLIENT_LOAD_SCREEN,
            CLIENT_INIT_OPCODE -> buf.skipBytes(3)
            else -> {
                logger.error { "Unhandled file request (opcode=$opcode, channel=$this)" }
                buf.skipBytes(buf.readableBytes())
            }
        }
    }

    private fun ByteBuf.readFileRequest(
        out: MutableList<Any>,
        urgent: Boolean
    ) {
        if (readableBytes() < Byte.SIZE_BYTES + Short.SIZE_BYTES) {
            resetReaderIndex()
            return
        }
        val archive = readUnsignedByte().toInt()
        val group = readUnsignedShort()
        val request = Js5Request(archive, group, urgent)
        out.add(request)
    }

    private companion object {
        private const val NORMAL_FILE_REQUEST = 0
        private const val URGENT_FILE_REQUEST = 1
        private const val CLIENT_INIT_GAME = 2
        private const val CLIENT_LOAD_SCREEN = 3
        private const val CLIENT_INIT_OPCODE = 6
    }
}
