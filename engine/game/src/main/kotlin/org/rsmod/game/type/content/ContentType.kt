package org.rsmod.game.type.content

public class ContentType(internal var internalId: Int?, internal val internalName: String) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String
        get() = internalName

    override fun toString(): String =
        "ContentType(internalName='$internalName', internalId=$internalId)"
}
