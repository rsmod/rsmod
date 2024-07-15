package org.rsmod.plugins.net.service.downstream

import io.netty.buffer.ByteBuf
import jakarta.inject.Singleton
import org.openrs2.crypto.StreamCipher
import org.rsmod.game.protocol.packet.FixedLengthPacketCodec

@Singleton
public class ExchangeSessionKeyCodec : FixedLengthPacketCodec<ServiceResponse.ExchangeSessionKey>(
    type = ServiceResponse.ExchangeSessionKey::class.java,
    opcode = 0,
    length = 8
) {

    override fun decode(buf: ByteBuf, cipher: StreamCipher): ServiceResponse.ExchangeSessionKey {
        val key = buf.readLong()
        return ServiceResponse.ExchangeSessionKey(key)
    }

    override fun encode(packet: ServiceResponse.ExchangeSessionKey, buf: ByteBuf, cipher: StreamCipher) {
        buf.writeLong(packet.key)
    }
}
