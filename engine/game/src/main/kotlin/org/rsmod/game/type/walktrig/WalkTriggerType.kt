package org.rsmod.game.type.walktrig

import kotlin.contracts.contract

public fun WalkTriggerType?.isType(type: WalkTriggerType): Boolean {
    contract { returns(true) implies (this@isType != null) }
    return this?.id == type.id
}

public class WalkTriggerType(
    internal var internalId: Int?,
    internal var internalName: String,
    internal var internalPriority: WalkTriggerPriority,
) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String
        get() = internalName

    public val priority: WalkTriggerPriority
        get() = internalPriority

    override fun toString(): String =
        "WalkTriggerType(internalName='$internalName', internalId=$internalId, priority=$priority)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WalkTriggerType

        if (internalPriority != other.internalPriority) return false
        if (internalId != other.internalId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = internalId ?: 0
        result = 31 * result + internalName.hashCode()
        result = 31 * result + internalPriority.hashCode()
        result = 31 * result + id
        result = 31 * result + internalNameGet.hashCode()
        return result
    }
}
