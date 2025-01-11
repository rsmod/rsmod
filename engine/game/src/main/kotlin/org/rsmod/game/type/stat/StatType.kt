package org.rsmod.game.type.stat

public class StatType(internal var internalId: Int?, internal var internalName: String) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String
        get() = internalName

    // TODO: Make value configurable.
    public val maxLevel: Int
        get() = 99

    override fun toString(): String =
        "StatType(internalName='$internalName', internalId=$internalId)"
}
