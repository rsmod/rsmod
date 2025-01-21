package org.rsmod.game.type.jingle

public class JingleType(internal var internalId: Int?, internal val internalName: String) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String
        get() = internalName

    override fun toString(): String =
        "JingleType(internalName='$internalName', internalId=$internalId)"
}
