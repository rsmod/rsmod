package org.rsmod.game.type.walktrig

import kotlin.contracts.contract

public fun WalkTriggerType?.isType(type: WalkTriggerType): Boolean {
    contract { returns(true) implies (this@isType != null) }
    return this?.id == type.id
}

public class WalkTriggerType(internal var internalId: Int?, internal val internalName: String) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String
        get() = internalName

    override fun toString(): String =
        "WalkTriggerType(internalName='$internalName', internalId=$internalId)"
}
