package org.rsmod.net.handshake

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

private val logger = InlineLogger()

class HandshakeDecoder @Inject constructor(
    private val handlers: HandshakeHandlerMap
) : ByteToMessageDecoder() {

    override fun decode(
        ctx: ChannelHandlerContext,
        buf: ByteBuf,
        out: MutableList<Any>
    ) {
        val opcode = buf.readByte().toInt()
        val handler = handlers[opcode]
        if (handler == null) {
            ctx.disconnect()
            logger.error { "Handler not found for handshake (opcode=$opcode)" }
            return
        }
        val decoder = handler.decoder
        val encoder = handler.encoder
        val adapter = handler.adapter
        val response = handler.response

        ctx.pipeline().replace(this, decoder.name, decoder.provider())
        ctx.pipeline().addLast(encoder.name, encoder.provider())
        ctx.pipeline().addLast(adapter.name, adapter.provider())
        ctx.pipeline().addLast(response.name, response.provider())
    }
}
