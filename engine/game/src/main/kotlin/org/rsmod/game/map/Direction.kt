package org.rsmod.game.map

import kotlin.math.atan2
import org.rsmod.map.CoordGrid
import org.rsmod.map.util.Bounds

public enum class Direction(public val id: Int, public val xOff: Int, public val zOff: Int) {
    NorthWest(0, -1, 1),
    North(1, 0, 1),
    NorthEast(2, 1, 1),
    West(3, -1, 0),
    East(4, 1, 0),
    SouthWest(5, -1, -1),
    South(6, 0, -1),
    SouthEast(7, 1, -1);

    public val angle: Int
        get() =
            when (this) {
                South -> 0
                SouthWest -> 256
                West -> 512
                NorthWest -> 768
                North -> 1024
                NorthEast -> 1280
                East -> 1536
                SouthEast -> 1792
            }

    public fun isCardinal(): Boolean = this in CARDINAL

    public fun isOrdinal(): Boolean = this in ORDINAL

    public operator fun component1(): Int = xOff

    public operator fun component2(): Int = zOff

    public companion object {
        public val CARDINAL: List<Direction> = listOf(South, North, West, East)

        public val ORDINAL: List<Direction> = listOf(SouthWest, NorthWest, SouthEast, NorthEast)

        public fun forId(id: Int): Direction? =
            when (id) {
                South.id -> South
                North.id -> North
                West.id -> West
                East.id -> East
                SouthWest.id -> SouthWest
                NorthWest.id -> NorthWest
                SouthEast.id -> SouthEast
                NorthEast.id -> NorthEast
                else -> null
            }

        public fun between(a: CoordGrid, b: CoordGrid): Direction? {
            check(a.level == b.level) { "Coordinates must be on same level." }
            val deltaX = b.x - a.x
            val deltaZ = b.z - a.z
            return when {
                deltaX > 0 && deltaZ > 0 -> NorthEast
                deltaX > 0 && deltaZ < 0 -> SouthEast
                deltaX < 0 && deltaZ > 0 -> NorthWest
                deltaX < 0 && deltaZ < 0 -> SouthWest
                deltaX > 0 -> East
                deltaX < 0 -> West
                deltaZ > 0 -> North
                deltaZ < 0 -> South
                else -> null
            }
        }

        /**
         * Calculates the angle between two [Bounds]s.
         *
         * The angle is determined by the difference in their `x` and `z` coordinates, and is
         * returned as an integer in the range [0..2047].
         * - 0 corresponds to South.
         * - 512 corresponds to West.
         * - 1024 corresponds to North.
         * - 1536 corresponds to East.
         *
         * The angle is calculated using the `atan2` function, converted to fit our respective unit
         * system.
         *
         * Clarification:
         *
         * This function is part of the [Direction] class rather than the [Bounds] class because the
         * angle it returns is closely tied to the specific angular unit system defined here,
         * represented by [Direction.angle].
         */
        public fun angleBetween(a: Bounds, b: Bounds): Int {
            val dx = a.fineCentreX - b.fineCentreX
            val dz = a.fineCentreZ - b.fineCentreZ
            return (atan2(dx, dz) * 325.94932345220167).toInt() and 2047
        }
    }
}

public fun CoordGrid.translate(direction: Direction, magnitude: Int = 1): CoordGrid =
    translate(direction.xOff * magnitude, direction.zOff * magnitude)
