package org.rsmod.plugins.net.login.downstream

import io.netty.buffer.ByteBuf
import org.openrs2.crypto.StreamCipher
import org.rsmod.protocol.packet.VariableByteLengthPacketCodec
import javax.inject.Singleton

@Singleton
class ConnectOkCodec : VariableByteLengthPacketCodec<LoginResponse.ConnectOk>(
    type = LoginResponse.ConnectOk::class.java,
    opcode = 2
) {

    override fun decode(buf: ByteBuf, cipher: StreamCipher): LoginResponse.ConnectOk {
        val rememberDevice = buf.readBoolean()
        val identifier = buf.readInt()
        val playerModLevel = buf.readByte().toInt()
        val playerMod = buf.readBoolean()
        val playerIndex = buf.readUnsignedShort()
        val playerMember = buf.readBoolean()
        val accountHash = buf.readLong()
        return LoginResponse.ConnectOk(
            rememberDevice = rememberDevice,
            playerModLevel = playerModLevel,
            playerMod = playerMod,
            playerIndex = playerIndex,
            playerMember = playerMember,
            accountHash = accountHash
        )
    }

    override fun encode(packet: LoginResponse.ConnectOk, buf: ByteBuf, cipher: StreamCipher) {
        buf.writeBoolean(packet.rememberDevice)
        buf.writeInt(0) // TODO: write device/client identifier
        buf.writeByte(packet.playerModLevel)
        buf.writeBoolean(packet.playerMod)
        buf.writeShort(packet.playerIndex)
        buf.writeBoolean(packet.playerMember)
        buf.writeLong(packet.accountHash)
    }
}
