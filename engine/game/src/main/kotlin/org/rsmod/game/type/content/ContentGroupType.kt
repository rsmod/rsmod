package org.rsmod.game.type.content

public class ContentGroupType(internal var internalId: Int?, internal val internalName: String) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String
        get() = internalName

    override fun toString(): String =
        "ContentGroupType(internalName='$internalName', internalId=$internalId)"
}
