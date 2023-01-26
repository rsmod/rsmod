package org.rsmod.plugins.api.prot.builder.login

import io.netty.buffer.ByteBuf

@DslMarker
private annotation class PacketBuilderDsl

@PacketBuilderDsl
class LoginPacketDecoderBuilder<T : LoginPacket> {

    private lateinit var decoder: (ByteBuf) -> T

    fun decode(decoder: (buf: ByteBuf) -> T) {
        this.decoder = decoder
    }

    fun build(): LoginPacketDecoder<T> {
        check(::decoder.isInitialized)
        return LoginPacketDecoder(decoder)
    }
}
