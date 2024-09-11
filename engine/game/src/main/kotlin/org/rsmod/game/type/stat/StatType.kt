package org.rsmod.game.type.stat

public class StatType(internal var internalId: Int?, internal var internalName: String) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String
        get() = internalName

    override fun toString(): String =
        "StatType(internalName='$internalName', internalId=$internalId)"
}
