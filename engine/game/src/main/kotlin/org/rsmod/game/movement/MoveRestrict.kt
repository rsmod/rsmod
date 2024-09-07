package org.rsmod.game.movement

import org.rsmod.pathfinder.collision.CollisionStrategy

public enum class MoveRestrict(public val id: Int) {
    /** Walks on normal terrain. */
    Normal(0),
    /** Walks only on blocked terrain, like water. */
    Blocked(1),
    /** Walks on either normal terrain, or blocked terrain. */
    BlockedNormal(2),
    /** Walks only inside buildings, or squares with roof collision flag. */
    Indoors(3),
    /** Walks only outside of buildings, or squares without roof collision flag. */
    Outdoors(4),
    /** Doesn't walk. */
    NoMove(5),
    /** Can walk through players or NPCs that'd normally block NPCs. */
    PassThru(6);

    public val collisionStrategy: CollisionStrategy?
        get() =
            when (this) {
                Normal -> CollisionStrategy.Normal
                Blocked -> CollisionStrategy.Blocked
                BlockedNormal -> CollisionStrategy.LineOfSight
                Indoors -> CollisionStrategy.Indoors
                Outdoors -> CollisionStrategy.Outdoors
                PassThru -> CollisionStrategy.Normal
                NoMove -> null
            }
}
