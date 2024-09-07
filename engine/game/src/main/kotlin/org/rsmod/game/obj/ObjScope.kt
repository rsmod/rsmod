package org.rsmod.game.obj

public enum class ObjScope(public val id: Int) {
    /** Obj was spawned publicly and _will not_ respawn when deleted */
    Temp(0),
    /** Obj was spawned publicly and _will_ respawn when deleted */
    Perm(1),
    /** Obj was spawned for a specific player(s) and _will not_ respawn when deleted */
    Private(2);

    public companion object {
        public operator fun get(id: Int): ObjScope =
            when (id) {
                Temp.id -> Temp
                Perm.id -> Perm
                Private.id -> Private
                else -> throw IllegalArgumentException("ObjScope with id `$id` does not exist.")
            }
    }
}
