package org.rsmod.plugins.api.net.builder.login

import io.netty.buffer.ByteBuf

class LoginPacketDecoderMap(
    val decoders: MutableMap<Class<out LoginPacket>, LoginPacketDecoder<*>> = mutableMapOf()
) {

    inline fun <reified T : LoginPacket> register(noinline read: (buf: ByteBuf) -> T) {
        val builder = LoginPacketDecoderBuilder<T>().apply { decode(read) }
        val decoder = builder.build()
        check(T::class.java !in decoders) {
            "Login packet decoder already defined (packet=${T::class.simpleName})."
        }
        decoders[T::class.java] = decoder
    }

    @Suppress("UNCHECKED_CAST")
    inline operator fun <reified T : LoginPacket> get(packet: Class<T>): LoginPacketDecoder<T>? {
        return decoders[packet] as? LoginPacketDecoder<T>
    }
}
