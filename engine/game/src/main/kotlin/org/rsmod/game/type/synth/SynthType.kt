package org.rsmod.game.type.synth

import org.rsmod.game.type.CacheType

public data class SynthType(override var internalId: Int?, override var internalName: String?) :
    CacheType() {
    override fun toString(): String =
        "SynthType(internalName='$internalName', internalId=$internalId)"
}
