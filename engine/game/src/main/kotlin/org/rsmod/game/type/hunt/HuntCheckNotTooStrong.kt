package org.rsmod.game.type.hunt

public enum class HuntCheckNotTooStrong(public val id: Int) {
    Off(0),
    OutsideWilderness(1);

    public companion object {
        public operator fun get(id: Int): HuntCheckNotTooStrong? =
            when (id) {
                Off.id -> Off
                OutsideWilderness.id -> OutsideWilderness
                else -> null
            }
    }
}
