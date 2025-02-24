package org.rsmod.game.type.mod

import kotlin.contracts.contract
import org.rsmod.game.type.CacheType

public data class ModGroup(
    internal var levels: Set<ModLevel>,
    internal var flags: Int,
    override var internalId: Int?,
    override var internalName: String?,
) : CacheType() {
    public constructor(
        levels: Set<ModLevel>,
        moderator: Boolean,
        administrator: Boolean,
        internalId: Int,
        internalName: String,
    ) : this(levels, pack(moderator, administrator), internalId, internalName)

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

public infix fun ModGroup?.hasAccessTo(level: ModLevel): Boolean {
    contract { returns(true) implies (this@hasAccessTo != null) }
    return this != null && level in levels
}
