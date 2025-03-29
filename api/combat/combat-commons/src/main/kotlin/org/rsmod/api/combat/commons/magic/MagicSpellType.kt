package org.rsmod.api.combat.commons.magic

public enum class MagicSpellType(public val id: Int) {
    Combat(0),
    Utility(1),
    Teleport(2),
    Other(3);

    public companion object {
        public operator fun get(id: Int): MagicSpellType? =
            when (id) {
                Combat.id -> Combat
                Utility.id -> Utility
                Teleport.id -> Teleport
                Other.id -> Other
                else -> null
            }
    }
}
