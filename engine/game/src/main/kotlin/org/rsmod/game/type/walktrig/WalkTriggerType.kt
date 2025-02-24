package org.rsmod.game.type.walktrig

import kotlin.contracts.contract
import org.rsmod.game.type.CacheType

public data class WalkTriggerType(
    internal var internalPriority: WalkTriggerPriority,
    override var internalId: Int?,
    override var internalName: String?,
) : CacheType() {
    public val priority: WalkTriggerPriority
        get() = internalPriority

    override fun toString(): String =
        "WalkTriggerType(internalName='$internalName', internalId=$internalId, priority=$priority)"
}

public fun WalkTriggerType?.isType(type: WalkTriggerType): Boolean {
    contract { returns(true) implies (this@isType != null) }
    return this?.id == type.id
}
