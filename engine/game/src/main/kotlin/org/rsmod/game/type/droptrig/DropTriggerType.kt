package org.rsmod.game.type.droptrig

import org.rsmod.game.type.CacheType

public data class DropTriggerType(
    override var internalId: Int?,
    override var internalName: String?,
) : CacheType() {
    override fun toString(): String =
        "DropTriggerType(internalName='$internalName', internalId=$internalId)"
}
