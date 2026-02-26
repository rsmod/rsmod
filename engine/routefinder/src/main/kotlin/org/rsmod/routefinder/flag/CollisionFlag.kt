package org.rsmod.routefinder.flag

public object CollisionFlag {
    public const val W_NW: Int = 0x1
    public const val W_N: Int = 0x2
    public const val W_NE: Int = 0x4
    public const val W_E: Int = 0x8
    public const val W_SE: Int = 0x10
    public const val W_S: Int = 0x20
    public const val W_SW: Int = 0x40
    public const val W_W: Int = 0x80
    public const val WALK_SCENARY: Int = 0x100
    public const val V_NW: Int = 0x200
    public const val V_N: Int = 0x400
    public const val V_NE: Int = 0x800
    public const val V_E: Int = 0x1000
    public const val V_SE: Int = 0x2000
    public const val V_S: Int = 0x4000
    public const val V_SW: Int = 0x8000
    public const val V_W: Int = 0x10000
    public const val VIS_SCENARY: Int = 0x20000
    public const val WR_GROUND_DECOR: Int = 0x40000
    /**
     * Custom flag dedicated to blocking NPCs. It should be noted that this is a custom flag, and
     * you do not need to use this. The pathfinder takes the flag as a custom option, so you may use
     * any other flag, this just defines a reliable constant to use
     */
    public const val BLOCK_NPCS: Int = 0x80000
    /**
     * Custom flag dedicated to blocking players, projectiles as well as NPCs. An example of a
     * monster to set this flag is Brawler. Note that it is unclear if this flag prevents NPCs, as
     * there is a separate flag option for it. This flag is similar to the one above, except it's
     * strictly for NPCs.
     */
    public const val BLOCK_PLAYERS: Int = 0x100000
    public const val WR_GRND: Int = 0x200000
    public const val WALL_NORTH_WEST_ROUTE_BLOCKER: Int = 0x400000
    public const val WALL_NORTH_ROUTE_BLOCKER: Int = 0x800000
    public const val WALL_NORTH_EAST_ROUTE_BLOCKER: Int = 0x1000000
    public const val WALL_EAST_ROUTE_BLOCKER: Int = 0x2000000
    public const val WALL_SOUTH_EAST_ROUTE_BLOCKER: Int = 0x4000000
    public const val WALL_SOUTH_ROUTE_BLOCKER: Int = 0x8000000
    public const val WALL_SOUTH_WEST_ROUTE_BLOCKER: Int = 0x10000000
    public const val WALL_WEST_ROUTE_BLOCKER: Int = 0x20000000
    public const val LOC_ROUTE_BLOCKER: Int = 0x40000000
    /**
     * Roof flag, used to bind NPCs to not leave the buildings they spawn in. This is a custom flag.
     */
    public const val ROOF: Int = 0x80000000.toInt()

    private const val FLOOR_BLOCKED = WR_GRND or WR_GROUND_DECOR
    public const val BLOCK_WEST: Int = W_E or WALK_SCENARY or FLOOR_BLOCKED
    public const val BLOCK_EAST: Int = W_W or WALK_SCENARY or FLOOR_BLOCKED
    public const val BLOCK_SOUTH: Int = W_N or WALK_SCENARY or FLOOR_BLOCKED
    public const val BLOCK_NORTH: Int = W_S or WALK_SCENARY or FLOOR_BLOCKED
    public const val BLOCK_SOUTH_WEST: Int = W_N or W_NE or W_E or WALK_SCENARY or FLOOR_BLOCKED
    public const val BLOCK_SOUTH_EAST: Int = W_NW or W_N or W_W or WALK_SCENARY or FLOOR_BLOCKED
    public const val BLOCK_NORTH_WEST: Int = W_E or W_SE or W_S or WALK_SCENARY or FLOOR_BLOCKED
    public const val BLOCK_NORTH_EAST: Int = W_S or W_SW or W_W or WALK_SCENARY or FLOOR_BLOCKED

    public const val BLOCK_NORTH_AND_SOUTH_EAST: Int =
        W_N or W_NE or W_E or W_SE or W_S or WALK_SCENARY or FLOOR_BLOCKED

    public const val BLOCK_NORTH_AND_SOUTH_WEST: Int =
        W_NW or W_N or W_S or W_SW or W_W or WALK_SCENARY or FLOOR_BLOCKED

    public const val BLOCK_NORTH_EAST_AND_WEST: Int =
        W_NW or W_N or W_NE or W_E or W_W or WALK_SCENARY or FLOOR_BLOCKED

    public const val BLOCK_SOUTH_EAST_AND_WEST: Int =
        W_E or W_SE or W_S or W_SW or W_W or WALK_SCENARY or FLOOR_BLOCKED

    /* Route blocker flags. These are used in ~550+ clients to generate paths through bankers and such. */

    public const val BLOCK_WEST_ROUTE_BLOCKER: Int =
        WALL_EAST_ROUTE_BLOCKER or LOC_ROUTE_BLOCKER or FLOOR_BLOCKED
    public const val BLOCK_EAST_ROUTE_BLOCKER: Int =
        WALL_WEST_ROUTE_BLOCKER or LOC_ROUTE_BLOCKER or FLOOR_BLOCKED
    public const val BLOCK_SOUTH_ROUTE_BLOCKER: Int =
        WALL_NORTH_ROUTE_BLOCKER or LOC_ROUTE_BLOCKER or FLOOR_BLOCKED
    public const val BLOCK_NORTH_ROUTE_BLOCKER: Int =
        WALL_SOUTH_ROUTE_BLOCKER or LOC_ROUTE_BLOCKER or FLOOR_BLOCKED

    public const val BLOCK_SOUTH_WEST_ROUTE_BLOCKER: Int =
        WALL_NORTH_ROUTE_BLOCKER or
            WALL_NORTH_EAST_ROUTE_BLOCKER or
            WALL_EAST_ROUTE_BLOCKER or
            LOC_ROUTE_BLOCKER or
            FLOOR_BLOCKED

    public const val BLOCK_SOUTH_EAST_ROUTE_BLOCKER: Int =
        WALL_NORTH_WEST_ROUTE_BLOCKER or
            WALL_NORTH_ROUTE_BLOCKER or
            WALL_WEST_ROUTE_BLOCKER or
            LOC_ROUTE_BLOCKER or
            FLOOR_BLOCKED

    public const val BLOCK_NORTH_WEST_ROUTE_BLOCKER: Int =
        WALL_EAST_ROUTE_BLOCKER or
            WALL_SOUTH_EAST_ROUTE_BLOCKER or
            WALL_SOUTH_ROUTE_BLOCKER or
            LOC_ROUTE_BLOCKER or
            FLOOR_BLOCKED

    public const val BLOCK_NORTH_EAST_ROUTE_BLOCKER: Int =
        WALL_SOUTH_ROUTE_BLOCKER or
            WALL_SOUTH_WEST_ROUTE_BLOCKER or
            WALL_WEST_ROUTE_BLOCKER or
            LOC_ROUTE_BLOCKER or
            FLOOR_BLOCKED

    public const val BLOCK_NORTH_AND_SOUTH_EAST_ROUTE_BLOCKER: Int =
        WALL_NORTH_ROUTE_BLOCKER or
            WALL_NORTH_EAST_ROUTE_BLOCKER or
            WALL_EAST_ROUTE_BLOCKER or
            WALL_SOUTH_EAST_ROUTE_BLOCKER or
            WALL_SOUTH_ROUTE_BLOCKER or
            LOC_ROUTE_BLOCKER or
            FLOOR_BLOCKED

    public const val BLOCK_NORTH_AND_SOUTH_WEST_ROUTE_BLOCKER: Int =
        WALL_NORTH_WEST_ROUTE_BLOCKER or
            WALL_NORTH_ROUTE_BLOCKER or
            WALL_SOUTH_ROUTE_BLOCKER or
            WALL_SOUTH_WEST_ROUTE_BLOCKER or
            WALL_WEST_ROUTE_BLOCKER or
            LOC_ROUTE_BLOCKER or
            FLOOR_BLOCKED

    public const val BLOCK_NORTH_EAST_AND_WEST_ROUTE_BLOCKER: Int =
        WALL_NORTH_WEST_ROUTE_BLOCKER or
            WALL_NORTH_ROUTE_BLOCKER or
            WALL_NORTH_EAST_ROUTE_BLOCKER or
            WALL_EAST_ROUTE_BLOCKER or
            WALL_WEST_ROUTE_BLOCKER or
            LOC_ROUTE_BLOCKER or
            FLOOR_BLOCKED

    public const val BLOCK_SOUTH_EAST_AND_WEST_ROUTE_BLOCKER: Int =
        WALL_EAST_ROUTE_BLOCKER or
            WALL_SOUTH_EAST_ROUTE_BLOCKER or
            WALL_SOUTH_ROUTE_BLOCKER or
            WALL_SOUTH_WEST_ROUTE_BLOCKER or
            WALL_WEST_ROUTE_BLOCKER or
            LOC_ROUTE_BLOCKER or
            FLOOR_BLOCKED
}
