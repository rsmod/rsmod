package org.rsmod.api.cache.util

import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.param.UnpackedParamType
import org.rsmod.game.type.varp.VarpTransmitLevel
import org.rsmod.game.type.varp.VarpType

/**
 * @param encodeFull If `true`, associated encoders will also encode server-side-only values into
 *   the cache.
 * @param clientParams A set of [ParamType.id] that will be used to filter params when packing them
 *   into the client-only cache. This is done to respect the flag: [UnpackedParamType.transmit].
 * @param clientVarps A set of [VarpType.id] that will be used to filter varps when packing varbits
 *   into the client-only cache. This is done to respect the transmission flag:
 *   [VarpTransmitLevel.Never].
 */
public data class EncoderContext
private constructor(
    public val encodeFull: Boolean,
    public val clientParams: Set<Int>,
    public val clientVarps: Set<Int>,
) {
    public val clientOnly: Boolean
        get() = !encodeFull

    public companion object {
        public fun client(params: Set<Int>, varps: Set<Int>): EncoderContext {
            return EncoderContext(encodeFull = false, params, varps)
        }

        public fun server(params: Set<Int>, varps: Set<Int>): EncoderContext {
            return EncoderContext(encodeFull = true, params, varps)
        }
    }
}
