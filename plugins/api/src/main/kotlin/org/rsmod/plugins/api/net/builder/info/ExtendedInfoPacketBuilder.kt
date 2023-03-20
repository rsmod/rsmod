package org.rsmod.plugins.api.net.builder.info

import io.netty.buffer.ByteBuf
import org.rsmod.game.model.mob.info.ExtendedInfo

@DslMarker
private annotation class PacketBuilderDsl

@PacketBuilderDsl
public class ExtendedInfoPacketBuilder<T : ExtendedInfo> {

    private lateinit var encoder: (T, ByteBuf) -> Unit

    public var bitmask: Int = -1

    public fun encode(encoder: (info: T, buf: ByteBuf) -> Unit) {
        this.encoder = encoder
    }

    public fun build(): ExtendedInfoPacketEncoder<T> {
        check(::encoder.isInitialized) { "`encode` must be set." }
        check(bitmask != -1) { "`bitmask` must be set." }
        return ExtendedInfoPacketEncoder(bitmask, encoder)
    }
}
