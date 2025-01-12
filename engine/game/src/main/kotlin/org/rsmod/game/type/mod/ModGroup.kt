package org.rsmod.game.type.mod

import kotlin.contracts.contract

public infix fun ModGroup?.hasAccessTo(level: ModLevel): Boolean {
    contract { returns(true) implies (this@hasAccessTo != null) }
    return this != null && level in levels
}

public class ModGroup
private constructor(
    internal var internalId: Int,
    internal var internalName: String,
    internal var flags: Int,
    internal var levels: Set<ModLevel>,
) {
    public constructor(
        internalId: Int,
        internalName: String,
        moderator: Boolean,
        administrator: Boolean,
        levels: Set<ModLevel>,
    ) : this(internalId, internalName, pack(moderator, administrator), levels)

    public val id: Int
        get() = internalId

    public val internalNameGet: String
        get() = internalName

    public val isClientMod: Boolean
        get() = flags and MODERATOR_FLAG != 0

    public val isClientAdmin: Boolean
        get() = flags and ADMINISTRATOR_FLAG != 0

    override fun toString(): String =
        "ModGroup(" +
            "internalId=$internalId, " +
            "internalName='$internalName', " +
            "administrator=$isClientAdmin, " +
            "moderator=$isClientMod, " +
            "flags=$flags" +
            ")"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModGroup) return false

        if (internalId != other.internalId) return false
        if (internalName != other.internalName) return false
        if (flags != other.flags) return false
        if (levels != other.levels) return false

        return true
    }

    override fun hashCode(): Int {
        var result = internalId
        result = 31 * result + internalName.hashCode()
        result = 31 * result + flags
        result = 31 * result + levels.hashCode()
        return result
    }

    public companion object {
        public const val MODERATOR_FLAG: Int = 0x1
        public const val ADMINISTRATOR_FLAG: Int = 0x2

        private fun pack(moderator: Boolean, administrator: Boolean): Int {
            var flags = 0
            if (moderator) {
                flags = flags or MODERATOR_FLAG
            }
            if (administrator) {
                flags = flags or ADMINISTRATOR_FLAG
            }
            return flags
        }
    }
}
