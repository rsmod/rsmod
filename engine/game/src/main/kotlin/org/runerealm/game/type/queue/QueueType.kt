package org.runerealm.game.type.queue

import org.runerealm.game.type.CacheType

public data class QueueType(override var internalId: Int?, override var internalName: String?) :
    CacheType() {
    override fun toString(): String =
        "QueueType(internalName='$internalName', internalId=$internalId)"
}
