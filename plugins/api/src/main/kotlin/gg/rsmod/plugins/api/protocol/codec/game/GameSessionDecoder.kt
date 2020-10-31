package gg.rsmod.plugins.api.protocol.codec.game

import com.github.michaelbull.logging.InlineLogger
import gg.rsmod.game.message.ClientPacketMessage
import gg.rsmod.game.message.ClientPacketStructureMap
import gg.rsmod.util.security.IsaacRandom
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

private val logger = InlineLogger()

sealed class PacketDecodeStage {
    object Opcode : PacketDecodeStage()
    object Length : PacketDecodeStage()
    object Payload : PacketDecodeStage()
}

class GameSessionDecoder(
    private val isaacRandom: IsaacRandom,
    private val structures: ClientPacketStructureMap,
    private var stage: PacketDecodeStage = PacketDecodeStage.Opcode,
    private var opcode: Int = -1,
    private var length: Int = 0
) : ByteToMessageDecoder() {

    override fun decode(
        ctx: ChannelHandlerContext,
        buf: ByteBuf,
        out: MutableList<Any>
    ) {
        when (stage) {
            PacketDecodeStage.Opcode -> buf.readOpcode(ctx.channel(), out)
            PacketDecodeStage.Length -> buf.readLength(out)
            PacketDecodeStage.Payload -> buf.readPayload(out)
        }
    }

    private fun ByteBuf.readOpcode(channel: Channel, out: MutableList<Any>) {
        opcode = readModifiedOpcode()
        val structure = structures[opcode]
        if (structure == null) {
            logger.error { "Structure for packet not defined (opcode=$opcode, channel=$channel)" }
            skipBytes(readableBytes())
            channel.close()
            return
        }

        length = structure.length
        if (length == 0) {
            readPayload(out)
            return
        }
        stage = if (length < 0) PacketDecodeStage.Length else PacketDecodeStage.Payload
    }

    private fun ByteBuf.readLength(out: MutableList<Any>) {
        length = readBytesRequired() ?: return
        if (length == 0) {
            readPayload(out)
        } else {
            stage = PacketDecodeStage.Payload
        }
    }

    private fun ByteBuf.readPayload(out: MutableList<Any>) {
        if (readableBytes() < length) {
            return
        }
        try {
            val structure = structures.getValue(opcode)
            val read = structure.read
            val handler = structure.handler
            if (read == null || handler == null) {
                skipBytes(length)
            } else {
                val payload = readBytes(length)
                val packet = read(payload)
                val message = ClientPacketMessage(packet, handler)
                out.add(message)
            }
        } finally {
            stage = PacketDecodeStage.Opcode
        }
    }

    private fun ByteBuf.readModifiedOpcode(): Int {
        return (readUnsignedByte().toInt() - isaacRandom.opcodeModifier()) and 0xFF
    }

    private fun ByteBuf.readBytesRequired(): Int? = when (length) {
        BYTE_VARIABLE_LENGTH -> if (readableBytes() < Byte.SIZE_BYTES) null else readUnsignedByte().toInt()
        SHORT_VARIABLE_LENGTH -> if (readableBytes() < Short.SIZE_BYTES) null else readUnsignedShort()
        else -> error("Unsupported packet length (length=$length)")
    }

    companion object {
        private const val BYTE_VARIABLE_LENGTH = -1
        private const val SHORT_VARIABLE_LENGTH = -2
    }
}
