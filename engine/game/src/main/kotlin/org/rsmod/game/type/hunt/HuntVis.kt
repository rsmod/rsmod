package org.rsmod.game.type.hunt

public enum class HuntVis(public val id: Int) {
    Off(0),
    LineOfSight(1),
    LineOfWalk(2);

    public companion object {
        public operator fun get(id: Int): HuntVis? =
            when (id) {
                Off.id -> Off
                LineOfSight.id -> LineOfSight
                LineOfWalk.id -> LineOfWalk
                else -> null
            }
    }
}
