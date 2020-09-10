package gg.rsmod.plugins.protocol.codec.game

import com.github.michaelbull.logging.InlineLogger
import gg.rsmod.game.action.ActionHandlerMap
import gg.rsmod.game.message.ClientPacketStructureMap
import gg.rsmod.util.IsaacRandom
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
    private val handlers: ActionHandlerMap,
    private var stage: PacketDecodeStage = PacketDecodeStage.Opcode,
    private var opcode: Int = -1,
    private var length: Int = 0,
    private var readAttempts: Int = 0
) : ByteToMessageDecoder() {

    override fun decode(
        ctx: ChannelHandlerContext,
        buf: ByteBuf,
        out: MutableList<Any>
    ) {
        when (stage) {
            PacketDecodeStage.Opcode -> buf.readOpcode(ctx.channel(), out)
            PacketDecodeStage.Length -> buf.readLength(ctx.channel(), out)
            PacketDecodeStage.Payload -> buf.readPayload(ctx.channel(), out)
        }
    }

    private fun ByteBuf.readOpcode(channel: Channel, out: MutableList<Any>) {
        opcode = readModifiedOpcode()
        val structure = structures[opcode]
        if (structure == null) {
            logger.error { "Structure for packet not defined (opcode=$opcode)" }
            return
        }

        length = structure.length
        if (length == 0) {
            readPayload(channel ,out)
            return
        }
        stage = if (length < 0) PacketDecodeStage.Length else PacketDecodeStage.Payload
    }

    private fun ByteBuf.readLength(channel: Channel, out: MutableList<Any>) {
        length = readBytesRequired() ?: return
        if (length == 0) {
            readPayload(channel ,out)
        } else {
            stage = PacketDecodeStage.Payload
        }
    }

    private fun ByteBuf.readPayload(channel: Channel, out: MutableList<Any>) {
        if (readableBytes() < length) {
            channel.incrementReadAttempts()
            return
        }
        try {
            val structure = structures.getValue(opcode)
            val read = structure.read
            if (read == null || structure.suppress) {
                skipBytes(length)
            } else {
                val payload = readBytes(length)
                val packet = read(payload)
                val handler = handlers[packet]
                if (handler != null) {
                    out.add(handler)
                } else {
                    logger.error { "Handler for action not defined (action=$packet)" }
                }
            }
        } finally {
            stage = PacketDecodeStage.Opcode
        }
    }

    private fun Channel.incrementReadAttempts() {
        readAttempts++
        if (readAttempts >= MAX_READ_ATTEMPTS) {
            logger.debug { "Read attempt limit reached... dropping connection (channel=$this)" }
            close()
        } else {
            logger.debug { "Increment read attempts (attempts=${readAttempts}, channel=$this)" }
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

        private const val MAX_READ_ATTEMPTS = 10
    }
}
