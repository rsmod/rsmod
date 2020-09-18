package gg.rsmod.plugins.protocol.codec.login

import com.github.michaelbull.logging.InlineLogger
import gg.rsmod.util.security.IsaacRandom
import io.guthix.buffer.writeStringCP1252
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import java.lang.Integer.max
import java.lang.Integer.min
import java.net.InetSocketAddress

private val logger = InlineLogger()

@ChannelHandler.Sharable
object LoginEncoder : MessageToByteEncoder<LoginResponse>() {

    private const val UNBOUND_CONNECTION_ADDRESS = "DNE"
    private const val PROFILE_TRANSFER_TIME_BUFFER = 3
    private const val MAX_CUSTOM_ERROR_LINES = 3

    override fun encode(
        ctx: ChannelHandlerContext,
        msg: LoginResponse,
        out: ByteBuf
    ) {
        logger.debug { "Encode login response (response=$msg, channel=${ctx.channel()})" }
        out.writeByte(msg.type.opcode)
        out.writeResponse(ctx.channel(), msg)
    }

    private fun ByteBuf.writeResponse(channel: Channel, response: LoginResponse): Unit = when (response) {
        is LoginResponse.Normal -> writeResponse(channel, response)
        is LoginResponse.Reconnect -> writeResponse(response)
        is LoginResponse.Transfer -> writeResponse(response)
        is LoginResponse.Restart -> { /* empty */ }
        is LoginResponse.Error -> writeResponse(response)
    }

    private fun ByteBuf.writeResponse(channel: Channel, response: LoginResponse.Normal) {
        val start = writerIndex() + Byte.SIZE_BYTES
        writeByte(0)
        writeBoolean(response.rememberDevice)
        if (response.rememberDevice) {
            writeRememberDevice(channel, response.encodeIsaac)
        } else {
            writeInt(0)
        }
        writeByte(response.privilege)
        writeBoolean(response.moderator)
        writeShort(response.playerIndex)
        writeBoolean(response.members)

        val length = writerIndex() - start
        setByte(start, length)
    }

    private fun ByteBuf.writeResponse(response: LoginResponse.Reconnect) {
        val gpi = response.gpi
        val start = writerIndex() + Short.SIZE_BYTES
        writeShort(0)
        gpi.write(this)

        val length = writerIndex() - start
        setShort(start, length)
    }

    private fun ByteBuf.writeResponse(response: LoginResponse.Transfer) {
        val seconds = min(255, max(0, response.secondsLeft - PROFILE_TRANSFER_TIME_BUFFER))
        writeByte(seconds)
    }

    private fun ByteBuf.writeResponse(response: LoginResponse.Error) {
        val errors = response.errors
        val messages = errors.subList(0, MAX_CUSTOM_ERROR_LINES)
        val length = messages.sumBy { it.length }

        if (errors.size > MAX_CUSTOM_ERROR_LINES) {
            logger.warn { "Login error response can only send a maximum of $MAX_CUSTOM_ERROR_LINES strings" }
        } else if (length >= 65535) {
            logger.warn { "Login error response messages exceed maximum short length value (length=$length)" }
        }

        writeShort(length + Short.SIZE_BYTES)
        messages.forEach {
            writeStringCP1252(it)
        }
    }

    private fun ByteBuf.writeRememberDevice(channel: Channel, isaac: IsaacRandom) {
        val address = (channel.remoteAddress() as? InetSocketAddress)?.hostName ?: UNBOUND_CONNECTION_ADDRESS
        val split = address.split(".").mapNotNull { it.toIntOrNull() }
        val ipv4 = split.size == 4
        if (!ipv4) {
            writeInt(0)
        } else {
            split.forEach { value ->
                val modified = (value + isaac.opcodeModifier()) and 0xFF
                writeByte(modified)
            }
        }
    }
}
