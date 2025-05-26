package org.rsmod.game.type.hunt

public enum class HuntType(public val id: Int) {
    Off(0),
    Player(1),
    Npc(2),
    Obj(3),
    Scenery(4);

    public companion object {
        public operator fun get(id: Int): HuntType? =
            when (id) {
                Off.id -> Off
                Player.id -> Player
                Npc.id -> Npc
                Obj.id -> Obj
                Scenery.id -> Scenery
                else -> null
            }
    }
}
