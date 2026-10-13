package org.runerealm.game.type.jingle

import org.runerealm.game.type.CacheType

public data class JingleType(override var internalId: Int?, override var internalName: String?) :
    CacheType() {
    override fun toString(): String =
        "JingleType(internalName='$internalName', internalId=$internalId)"
}
