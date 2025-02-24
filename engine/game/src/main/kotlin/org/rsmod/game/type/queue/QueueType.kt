package org.rsmod.game.type.queue

import org.rsmod.game.type.CacheType

public data class QueueType(override var internalId: Int?, override var internalName: String?) :
    CacheType() {
    override fun toString(): String =
        "QueueType(internalName='$internalName', internalId=$internalId)"
}
