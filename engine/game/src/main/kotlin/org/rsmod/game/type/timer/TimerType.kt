package org.rsmod.game.type.timer

import org.rsmod.game.type.CacheType

public data class TimerType(override var internalId: Int?, override var internalName: String?) :
    CacheType() {
    override fun toString(): String =
        "TimerType(internalName='$internalName', internalId=$internalId)"
}
