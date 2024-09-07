package org.rsmod.game.type.mod

public class ModLevel(internal var internalId: Int, internal var internalName: String) {
    public val id: Int
        get() = internalId

    public val internalNameGet: String
        get() = internalName

    override fun toString(): String = "ModLevel(internalId=$internalId, internalName=$internalName)"
}
