package org.rsmod.game.pathfinder.flag

public object DirectionFlag {

    public const val NORTH: Int = 0x1
    public const val EAST: Int = 0x2
    public const val SOUTH: Int = 0x4
    public const val WEST: Int = 0x8

    public const val SOUTH_WEST: Int = WEST or SOUTH
    public const val NORTH_WEST: Int = WEST or NORTH
    public const val SOUTH_EAST: Int = EAST or SOUTH
    public const val NORTH_EAST: Int = EAST or NORTH
}
