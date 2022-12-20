@file:Suppress("unused")

package org.rsmod.game.pathfinder.collision

public object CollisionStrategies {
    public val Normal: CollisionStrategy = NormalFlagCollision()
    public val Blocked: CollisionStrategy = BlockedFlagCollision()
    public val Fly: CollisionStrategy = LineOfSightBlockFlagCollision()
    public val Indoors: CollisionStrategy = IndoorsFlagCollision()
}
