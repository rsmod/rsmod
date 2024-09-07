package org.rsmod.game.type.obj

public enum class Dummyitem(public val id: Int) {
    GraphicOnly(id = 1),
    InvOnly(id = 2);

    public companion object {
        public operator fun get(id: Int): Dummyitem? =
            when (id) {
                GraphicOnly.id -> GraphicOnly
                InvOnly.id -> InvOnly
                else -> null
            }
    }
}
