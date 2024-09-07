package org.rsmod.game.type.category

public class CategoryType(internal var internalId: Int?, internal val internalName: String) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String
        get() = internalName

    override fun toString(): String =
        "CategoryType(internalName='$internalName', internalId=$internalId)"
}
