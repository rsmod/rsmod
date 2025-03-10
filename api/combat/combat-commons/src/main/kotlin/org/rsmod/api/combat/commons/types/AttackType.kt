package org.rsmod.api.combat.commons.types

public enum class AttackType(public val id: Int) {
    /* `id` 0 is reserved for "null" value. */
    Stab(1),
    Slash(2),
    Crush(3),
    Magic(4),
    Light(5),
    Standard(6),
    Heavy(7);

    public val isRanged: Boolean
        get() = this == Light || this == Standard || this == Heavy

    public val isMagic: Boolean
        get() = this == Magic

    public val isMelee: Boolean
        get() = this == Stab || this == Slash || this == Crush

    public companion object {
        public operator fun get(id: Int): AttackType? =
            when (id) {
                Stab.id -> Stab
                Slash.id -> Slash
                Crush.id -> Crush
                Magic.id -> Magic
                Light.id -> Light
                Standard.id -> Standard
                Heavy.id -> Heavy
                else -> null
            }
    }
}
