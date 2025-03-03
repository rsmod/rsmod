package org.rsmod.game.hit

public enum class HitType(public val id: Int) {
    Typeless(0),
    Melee(1),
    Ranged(2),
    Magic(3);

    public companion object {
        public operator fun get(id: Int): HitType? =
            when (id) {
                Typeless.id -> Typeless
                Melee.id -> Melee
                Ranged.id -> Ranged
                Magic.id -> Magic
                else -> null
            }
    }
}
