package org.runerealm.game.movement

import org.runerealm.routefinder.flag.CollisionFlag

public enum class BlockWalk(public val id: Int) {
    None(0),
    Npc(1),
    All(2);

    public val collisionFlag: Int?
        get() =
            when (this) {
                Npc -> CollisionFlag.BLOCK_NPCS
                All -> CollisionFlag.BLOCK_PLAYERS or CollisionFlag.BLOCK_NPCS
                None -> null
            }
}
