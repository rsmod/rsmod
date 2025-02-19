package org.rsmod.game.type.varp

public enum class VarpLifetime(public val id: Int) {
    /** Varp is not saved on log-out. */
    Temp(0),
    /** Varp is saved on log-out. */
    Perm(1);

    public companion object {
        public operator fun get(id: Int): VarpLifetime? =
            when (id) {
                Temp.id -> Temp
                Perm.id -> Perm
                else -> null
            }
    }
}
