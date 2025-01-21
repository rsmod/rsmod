package org.rsmod.game.type.stat

public class StatType(internal var internalId: Int?, internal var internalName: String) {
    public val id: Int
        get() = internalId ?: error("`internalId` must not be null.")

    public val internalNameGet: String
        get() = internalName

    // TODO: Make values configurable.

    public val maxLevel: Int
        get() = 99

    public val displayName: String = internalNameGet.replaceFirstChar(Char::titlecase)

    public val unreleased: Boolean
        get() = internalNameGet == "unreleased" || internalNameGet == "sailing"

    override fun toString(): String =
        "StatType(internalName='$internalName', internalId=$internalId)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StatType

        if (internalId != other.internalId) return false
        if (internalName != other.internalName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = internalId ?: 0
        result = 31 * result + internalName.hashCode()
        return result
    }
}
