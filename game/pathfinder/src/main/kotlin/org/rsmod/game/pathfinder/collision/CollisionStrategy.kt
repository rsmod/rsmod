package org.rsmod.game.pathfinder.collision

public interface CollisionStrategy {

    public fun canMove(tileFlag: Int, blockFlag: Int): Boolean
}
