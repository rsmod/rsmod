package org.rsmod.plugins.net.login.downstream

import io.netty.buffer.ByteBuf
import jakarta.inject.Singleton
import org.openrs2.crypto.StreamCipher
import org.rsmod.game.protocol.packet.VariableByteLengthPacketCodec

@Singleton
public class ConnectOkCodec : VariableByteLengthPacketCodec<LoginResponse.ConnectOk>(
    type = LoginResponse.ConnectOk::class.java,
    opcode = 2
) {

    override fun decode(buf: ByteBuf, cipher: StreamCipher): LoginResponse.ConnectOk {
        val decodeDeviceIdentifier = buf.readBoolean()
        val deviceIdentifier = if (decodeDeviceIdentifier) {
            var identifier = 0
            identifier = identifier or (((buf.readUnsignedByte() - cipher.nextInt()) and 0xFF) shl 24)
            identifier = identifier or (((buf.readUnsignedByte() - cipher.nextInt()) and 0xFF) shl 16)
            identifier = identifier or (((buf.readUnsignedByte() - cipher.nextInt()) and 0xFF) shl 8)
            identifier = identifier or ((buf.readUnsignedByte() - cipher.nextInt()) and 0xFF)
            identifier
        } else {
            buf.skipBytes(Int.SIZE_BYTES)
            null
        }
        val playerModLevel = buf.readByte().toInt()
        val playerMod = buf.readBoolean()
        val playerIndex = buf.readUnsignedShort()
        val playerMember = buf.readBoolean()
        val accountHash = buf.readLong()
        return LoginResponse.ConnectOk(
            deviceLinkIdentifier = deviceIdentifier,
            playerModLevel = playerModLevel,
            playerMod = playerMod,
            playerIndex = playerIndex,
            playerMember = playerMember,
            accountHash = accountHash,
            cipher = cipher
        )
    }

    override fun encode(packet: LoginResponse.ConnectOk, buf: ByteBuf, cipher: StreamCipher) {
        buf.writeBoolean(packet.deviceLinkIdentifier != null)
        if (packet.deviceLinkIdentifier != null) {
            val identifier = IntArray(4)
            identifier[0] = packet.deviceLinkIdentifier shr 24
            identifier[1] = packet.deviceLinkIdentifier shr 16
            identifier[2] = packet.deviceLinkIdentifier shr 8
            identifier[3] = packet.deviceLinkIdentifier
            for (byte in identifier) {
                buf.writeByte(byte + packet.cipher.nextInt())
            }
        } else {
            buf.writeInt(0)
        }
        buf.writeByte(packet.playerModLevel)
        buf.writeBoolean(packet.playerMod)
        buf.writeShort(packet.playerIndex)
        buf.writeBoolean(packet.playerMember)
        buf.writeLong(packet.accountHash)
    }
}
