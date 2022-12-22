package org.rsmod.protocol

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import org.rsmod.crypto.NopStreamCipher
import org.rsmod.crypto.StreamCipher
import org.rsmod.protocol.packet.Packet
import org.rsmod.protocol.packet.PacketCodec

public class ProtocolEncoder(
    public var protocol: Protocol,
    public var cipher: StreamCipher = NopStreamCipher
) : MessageToByteEncoder<Packet>(Packet::class.java) {

    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: ByteBuf) {
        val encoder = msg.encoder()
        out.writeByte(encoder.opcode + cipher.nextInt())

        val lengthWriterIndex = out.writerIndex()
        encoder.offsetLength(out)

        val payloadWriterIndex = out.writerIndex()
        encoder.encode(msg, out, cipher)

        val payloadLength = out.writerIndex() - payloadWriterIndex
        encoder.setLength(out, lengthWriterIndex, payloadLength)
    }

    override fun allocateBuffer(ctx: ChannelHandlerContext, msg: Packet, preferDirect: Boolean): ByteBuf {
        val encoder = msg.encoder()
        return encoder.allocEncodeBuffer(ctx.alloc(), msg, preferDirect)
    }

    private fun <T : Packet> T.encoder(): PacketCodec<T> {
        return protocol.getEncoder(javaClass) ?: error("Encoder not found for packet type $javaClass")
    }
}
