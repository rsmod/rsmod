package org.rsmod.protocol.game

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.openrs2.crypto.NopStreamCipher
import org.openrs2.crypto.StreamCipher
import org.rsmod.protocol.game.packet.PacketCodec

private sealed class Stage {
    object ReadOpcode : Stage()
    object ReadLength : Stage()
    object ReadPayload : Stage()
}

public class ProtocolDecoder(
    public var protocol: Protocol,
    public var cipher: StreamCipher = NopStreamCipher
) : ByteToMessageDecoder() {

    private var stage: Stage = Stage.ReadOpcode
    private var length = 0

    private lateinit var decoder: PacketCodec<*>

    init {
        isSingleDecode = true
    }

    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, out: MutableList<Any>) {
        if (stage == Stage.ReadOpcode) {
            if (!input.isReadable) return
            val opcode = (input.readUnsignedByte().toInt() - cipher.nextInt()) and 0xFF
            decoder = protocol.getDecoder(opcode) ?: error("Decoder not found for opcode $opcode.")
            stage = Stage.ReadLength
        }

        if (stage == Stage.ReadLength) {
            if (!decoder.isLengthReadable(input)) return
            length = decoder.readLength(input)
            stage = Stage.ReadPayload
        }

        if (stage == Stage.ReadPayload) {
            if (input.readableBytes() < length) return
            val payload = input.readSlice(length)
            val packet = decoder.decode(payload, cipher)
            check(!payload.isReadable) {
                "Decoder did not fully read payload. " +
                    "(read ${payload.readerIndex()} bytes, left with ${payload.readableBytes()})"
            }
            out += packet
            stage = Stage.ReadOpcode
        }
    }
}
