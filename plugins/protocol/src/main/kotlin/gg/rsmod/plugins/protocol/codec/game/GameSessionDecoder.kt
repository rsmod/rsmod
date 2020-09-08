package gg.rsmod.plugins.protocol.codec.game

import com.github.michaelbull.logging.InlineLogger
import gg.rsmod.game.message.ClientPacketStructureMap
import gg.rsmod.util.IsaacRandom
import io.netty.buffer.ByteBuf
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
            PacketDecodeStage.Opcode -> buf.readOpcode()
            PacketDecodeStage.Length -> buf.readLength(out)
            PacketDecodeStage.Payload -> buf.readPayload(out)
        }
    }

    private fun ByteBuf.readOpcode() {
        opcode = (readUnsignedByte().toInt() - isaacRandom.opcodeModifier) and 0xFF

        val structure = structures[opcode]
        if (structure == null) {
            logger.error { "Structure for packet not defined (opcode=$opcode)" }
            return
        }

        length = structure.length
        stage = when {
            length == 0 -> PacketDecodeStage.Opcode
            length > 0 -> PacketDecodeStage.Payload
            else -> PacketDecodeStage.Length
        }
    }

    private fun ByteBuf.readLength(out: MutableList<Any>) {
        if (length == SHORT_VARIABLE_LENGTH && readableBytes() < Short.SIZE_BYTES) {
            return
        }
        length = when (length) {
            BYTE_VARIABLE_LENGTH -> readUnsignedByte().toInt()
            else -> readUnsignedShort()
        }

        if (length == 0) {
            readPayload(out)
        } else {
            stage = PacketDecodeStage.Payload
        }
    }

    private fun ByteBuf.readPayload(out: MutableList<Any>) {
        if (readableBytes() < length) {
            // TODO: max attempts before disconnecting player?
            return
        }
        try {
            val structure = structures.getValue(opcode)

            val payload = readBytes(length)
            val packet = structure.read(payload)
            // TODO: add packet handler to out list
        } finally {
            stage = PacketDecodeStage.Opcode
        }
    }

    companion object {
        private const val BYTE_VARIABLE_LENGTH = -1
        private const val SHORT_VARIABLE_LENGTH = -2
    }
}
