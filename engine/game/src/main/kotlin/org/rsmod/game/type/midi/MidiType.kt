package org.rsmod.game.type.midi

import org.rsmod.game.type.CacheType

public data class MidiType(override var internalId: Int?, override var internalName: String?) :
    CacheType() {
    override fun toString(): String =
        "MidiType(internalName='$internalName', internalId=$internalId)"
}
