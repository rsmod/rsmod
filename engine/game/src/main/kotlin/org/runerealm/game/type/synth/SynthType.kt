package org.runerealm.game.type.synth

import org.runerealm.game.type.CacheType

public data class SynthType(override var internalId: Int?, override var internalName: String?) :
    CacheType() {
    override fun toString(): String =
        "SynthType(internalName='$internalName', internalId=$internalId)"
}
