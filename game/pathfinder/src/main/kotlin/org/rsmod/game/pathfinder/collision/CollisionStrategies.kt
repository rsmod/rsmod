@file:Suppress("unused")

package org.rsmod.game.pathfinder.collision

public object CollisionStrategies {
    public val Normal: CollisionStrategy = NormalBlockFlagCollision()
    public val RoofBound: CollisionStrategy = RoofBoundFlagCollision()
    public val WaterBound: CollisionStrategy = WaterBoundFlagCollision()
    public val Fly: CollisionStrategy = LineOfSightBlockFlagCollision()
}
