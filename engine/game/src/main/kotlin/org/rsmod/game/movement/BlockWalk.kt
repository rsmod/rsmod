package org.rsmod.game.movement

import org.rsmod.pathfinder.flag.CollisionFlag

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
