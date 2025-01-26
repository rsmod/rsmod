package org.rsmod.api.cache.util

import org.rsmod.game.type.util.ParamMap

internal fun ParamMap.filterTransmit(clientParams: Set<Int>): ParamMap {
    val keys = checkNotNull(typedMap?.keys) { "`typedMap` required. (paramMap=$this)" }
    val filteredKeys = keys.filter(clientParams::contains)
    val filteredPrimitives = primitiveMap.filterKeys(filteredKeys::contains)
    val filteredTyped = typedMap?.filterKeys(filteredKeys::contains)
    return ParamMap(filteredPrimitives, filteredTyped)
}

internal fun ParamMap.filterTransmit(encoderCtx: EncoderContext): ParamMap =
    if (encoderCtx.encodeFull) this else filterTransmit(encoderCtx.clientParams)
