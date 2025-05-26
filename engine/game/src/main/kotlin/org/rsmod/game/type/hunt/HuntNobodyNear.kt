package org.rsmod.game.type.hunt

public enum class HuntNobodyNear(public val id: Int) {
    KeepHunting(0),
    PauseHunt(1);

    public companion object {
        public operator fun get(id: Int): HuntNobodyNear? =
            when (id) {
                KeepHunting.id -> KeepHunting
                PauseHunt.id -> PauseHunt
                else -> null
            }
    }
}
