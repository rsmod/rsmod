package org.rsmod.plugins.api.net.builder.info

import org.rsmod.game.model.mob.info.ExtendedInfo
import org.rsmod.plugins.api.net.info.ExtendedPlayerInfo

public class ExtendedInfoEncoderMap(
    public val player: EncoderMap<ExtendedPlayerInfo> = EncoderMap()
) {

    public class EncoderMap<T : ExtendedInfo>(
        public val encoders: MutableMap<Class<out T>, ExtendedInfoPacketEncoder<*>> = mutableMapOf(),
        public val order: MutableList<Class<out T>> = mutableListOf()
    ) {

        public inline fun <reified S : T> register(
            noinline init: (ExtendedInfoPacketBuilder<S>).() -> Unit
        ) {
            val builder = ExtendedInfoPacketBuilder<S>().apply(init)
            val encoder = builder.build()
            check(S::class.java !in encoders) {
                "Extended-info packet encoder already defined. (info=${S::class.simpleName})"
            }
            encoders[S::class.java] = encoder
        }

        @Suppress("UNCHECKED_CAST")
        public inline operator fun <reified S : T> get(info: S): ExtendedInfoPacketEncoder<S>? {
            return encoders[info::class.java] as? ExtendedInfoPacketEncoder<S>
        }
    }
}
