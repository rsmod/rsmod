package org.rsmod.api.cache.util

/**
 * @param encodeFull If `true`, associated encoders will also encode server-side-only values into
 *   the cache.
 * @param clientParams A set of [org.rsmod.game.type.param.ParamType.id] that will be used to filter
 *   params when packing them into the client-only cache. This is done to respect the flag:
 *   [org.rsmod.game.type.param.UnpackedParamType.transmit]
 */
public data class EncoderContext(
    public val encodeFull: Boolean,
    public val clientParams: Set<Int>,
) {
    public val clientOnly: Boolean
        get() = !encodeFull
}
