package org.rsmod.plugins.api.net.builder.login

import io.netty.buffer.ByteBuf

@DslMarker
private annotation class PacketBuilderDsl

@PacketBuilderDsl
public class LoginPacketDecoderBuilder<T : LoginPacket> {

    private lateinit var decoder: (ByteBuf) -> T

    public fun decode(decoder: (buf: ByteBuf) -> T) {
        this.decoder = decoder
    }

    public fun build(): LoginPacketDecoder<T> {
        check(::decoder.isInitialized)
        return LoginPacketDecoder(decoder)
    }
}
