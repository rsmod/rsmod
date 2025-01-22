package org.rsmod.game.type.inv

public enum class InvScope(public val id: Int) {
    /** Temporary inventories that do not save. (e.g., clue scrolls) */
    Temp(0),
    /** Persistent inventories that will save. (e.g., bank) */
    Perm(1),
    /** Global Inventories that are shared. (e.g., shops) */
    Shared(2),
}
