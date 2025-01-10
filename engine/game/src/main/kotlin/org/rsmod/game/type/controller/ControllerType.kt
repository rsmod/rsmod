package org.rsmod.game.type.controller

public class ControllerType(internal var internalId: Int?, internal val internalName: String) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String
        get() = internalName

    override fun toString(): String =
        "ControllerType(internalName='$internalName', internalId=$internalId)"
}
