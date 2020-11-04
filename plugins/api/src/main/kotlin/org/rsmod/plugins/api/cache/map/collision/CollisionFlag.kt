package org.rsmod.plugins.api.cache.map.collision

/* credits: Graham Edgecombe */
object CollisionFlag {
    const val FLAG_WALL_NORTH_WEST = 0x1
    const val FLAG_WALL_NORTH = 0x2
    const val FLAG_WALL_NORTH_EAST = 0x4
    const val FLAG_WALL_EAST = 0x8
    const val FLAG_WALL_SOUTH_EAST = 0x10
    const val FLAG_WALL_SOUTH = 0x20
    const val FLAG_WALL_SOUTH_WEST = 0x40
    const val FLAG_WALL_WEST = 0x80
    const val FLAG_OBJECT = 0x100
    const val FLAG_WALL_NORTH_WEST_PROJECTILE_BLOCKER = 0x200
    const val FLAG_WALL_NORTH_PROJECTILE_BLOCKER = 0x400
    const val FLAG_WALL_NORTH_EAST_PROJECTILE_BLOCKER = 0x800
    const val FLAG_WALL_EAST_PROJECTILE_BLOCKER = 0x1000
    const val FLAG_WALL_SOUTH_EAST_PROJECTILE_BLOCKER = 0x2000
    const val FLAG_WALL_SOUTH_PROJECTILE_BLOCKER = 0x4000
    const val FLAG_WALL_SOUTH_WEST_PROJECTILE_BLOCKER = 0x8000
    const val FLAG_WALL_WEST_PROJECTILE_BLOCKER = 0x10000
    const val FLAG_OBJECT_PROJECTILE_BLOCKER = 0x20000
    const val FLAG_TILE_DECORATION = 0x40000
    // const val FLAG_UNUSED = 0x80000 /* server-side only */
    const val FLAG_NON_WATER_TILE = 0x100000 /* server-side only */
    const val FLAG_TILE = 0x200000
    const val FLAG_WALL_NORTH_WEST_ROUTE_BLOCKER = 0x400000
    const val FLAG_WALL_NORTH_ROUTE_BLOCKER = 0x800000
    const val FLAG_WALL_NORTH_EAST_ROUTE_BLOCKER = 0x1000000
    const val FLAG_WALL_EAST_ROUTE_BLOCKER = 0x2000000
    const val FLAG_WALL_SOUTH_EAST_ROUTE_BLOCKER = 0x4000000
    const val FLAG_WALL_SOUTH_ROUTE_BLOCKER = 0x8000000
    const val FLAG_WALL_SOUTH_WEST_ROUTE_BLOCKER = 0x10000000
    const val FLAG_WALL_WEST_ROUTE_BLOCKER = 0x20000000
    const val FLAG_OBJECT_ROUTE_BLOCKER = 0x40000000
    // const val FLAG_UNUSED = 0x80000000 /* server-side only */
}
