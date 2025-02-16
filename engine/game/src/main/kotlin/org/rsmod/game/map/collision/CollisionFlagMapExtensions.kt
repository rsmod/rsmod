package org.rsmod.game.map.collision

import org.rsmod.game.loc.LocInfo
import org.rsmod.game.type.loc.UnpackedLocType
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey
import org.rsmod.routefinder.collision.CollisionFlagMap
import org.rsmod.routefinder.flag.CollisionFlag
import org.rsmod.routefinder.loc.LocAngleConstants
import org.rsmod.routefinder.loc.LocShapeConstants

private typealias LocShapes = LocShapeConstants

public fun CollisionFlagMap.isZoneValid(zone: ZoneKey): Boolean = isZoneValid(zone.toCoords())

public fun CollisionFlagMap.isZoneValid(coords: CoordGrid): Boolean =
    isZoneAllocated(coords.x, coords.z, coords.level)

public operator fun CollisionFlagMap.get(coords: CoordGrid): Int =
    get(coords.x, coords.z, coords.level)

public operator fun CollisionFlagMap.set(coords: CoordGrid, mask: Int): Unit =
    set(coords.x, coords.z, coords.level, mask)

public fun CollisionFlagMap.add(coords: CoordGrid, mask: Int): Unit =
    add(coords.x, coords.z, coords.level, mask)

public fun CollisionFlagMap.remove(coords: CoordGrid, mask: Int): Unit =
    remove(coords.x, coords.z, coords.level, mask)

public fun CollisionFlagMap.addLoc(loc: LocInfo, type: UnpackedLocType): Unit =
    toggleLoc(loc, type, add = true)

public fun CollisionFlagMap.removeLoc(loc: LocInfo, type: UnpackedLocType): Unit =
    toggleLoc(loc, type, add = false)

public fun CollisionFlagMap.toggleLoc(loc: LocInfo, type: UnpackedLocType, add: Boolean) {
    toggleLoc(
        coords = loc.coords,
        width = type.width,
        length = type.length,
        shape = loc.shapeId,
        angle = loc.angleId,
        blockWalk = type.blockWalk,
        blockRange = type.blockRange,
        breakRouteFinding = type.breakRouteFinding,
        add = add,
    )
}

public fun CollisionFlagMap.toggleLoc(
    coords: CoordGrid,
    width: Int,
    length: Int,
    shape: Int,
    angle: Int,
    blockWalk: Int,
    blockRange: Boolean,
    breakRouteFinding: Boolean,
    add: Boolean,
) {
    var rotatedWidth = width
    var rotatedLength = length
    if (angle == LocAngleConstants.NORTH || angle == LocAngleConstants.SOUTH) {
        rotatedWidth = length
        rotatedLength = width
    }
    if (shape == LocShapes.GROUND_DECOR) {
        if (blockWalk == 1) {
            toggleGroundDecor(coords, add)
        }
    } else if (shape != LocShapes.CENTREPIECE_STRAIGHT && shape != LocShapes.CENTREPIECE_DIAGONAL) {
        val primary = shape >= LocShapes.ROOF_STRAIGHT || shape == LocShapes.WALL_DIAGONAL
        val wall =
            shape == LocShapes.WALL_STRAIGHT ||
                shape == LocShapes.WALL_DIAGONAL_CORNER ||
                shape == LocShapes.WALL_L ||
                shape == LocShapes.WALL_SQUARE_CORNER
        if (primary && blockWalk != 0) {
            toggleGround(coords, rotatedWidth, rotatedLength, breakRouteFinding, blockRange, add)
        } else if (wall && blockWalk != 0) {
            toggleWall(coords, angle, shape, blockRange, add)
        }
    } else {
        if (blockWalk != 0) {
            toggleGround(coords, rotatedWidth, rotatedLength, breakRouteFinding, blockRange, add)
        }
    }
}

private fun CollisionFlagMap.toggleGround(
    coords: CoordGrid,
    width: Int,
    length: Int,
    blockRoute: Boolean,
    blockRange: Boolean,
    add: Boolean,
) {
    for (x in 0 until width) {
        for (z in 0 until length) {
            val translate = coords.translate(x, z)
            toggle(translate, CollisionFlag.LOC, add)
            if (blockRange) {
                toggle(translate, CollisionFlag.LOC_PROJ_BLOCKER, add)
            }
            if (blockRoute) {
                toggle(translate, CollisionFlag.LOC_ROUTE_BLOCKER, add)
            }
        }
    }
}

@Suppress("CascadeIf")
private fun CollisionFlagMap.toggleWall(
    coords: CoordGrid,
    angle: Int,
    shape: Int,
    blockRange: Boolean,
    add: Boolean,
) {
    if (shape == LocShapes.WALL_STRAIGHT) {
        when (angle) {
            0 -> {
                toggle(coords, CollisionFlag.WALL_WEST, add)
                toggle(coords.translate(-1, 0), CollisionFlag.WALL_EAST, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.WALL_WEST_PROJ_BLOCKER, add)
                    toggle(coords.translate(-1, 0), CollisionFlag.WALL_EAST_PROJ_BLOCKER, add)
                }
            }
            1 -> {
                toggle(coords, CollisionFlag.WALL_NORTH, add)
                toggle(coords.translate(0, 1), CollisionFlag.WALL_SOUTH, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.WALL_NORTH_PROJ_BLOCKER, add)
                    toggle(coords.translate(0, 1), CollisionFlag.WALL_SOUTH_PROJ_BLOCKER, add)
                }
            }
            2 -> {
                toggle(coords, CollisionFlag.WALL_EAST, add)
                toggle(coords.translate(1, 0), CollisionFlag.WALL_WEST, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.WALL_EAST_PROJ_BLOCKER, add)
                    toggle(coords.translate(1, 0), CollisionFlag.WALL_WEST_PROJ_BLOCKER, add)
                }
            }
            3 -> {
                toggle(coords, CollisionFlag.WALL_SOUTH, add)
                toggle(coords.translate(0, -1), CollisionFlag.WALL_NORTH, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.WALL_SOUTH_PROJ_BLOCKER, add)
                    toggle(coords.translate(0, -1), CollisionFlag.WALL_NORTH_PROJ_BLOCKER, add)
                }
            }
        }
    } else if (shape == LocShapes.WALL_DIAGONAL_CORNER || shape == LocShapes.WALL_SQUARE_CORNER) {
        when (angle) {
            0 -> {
                toggle(coords, CollisionFlag.WALL_NORTH_WEST, add)
                toggle(coords.translate(-1, 1), CollisionFlag.WALL_SOUTH_EAST, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.WALL_NORTH_WEST_PROJ_BLOCKER, add)
                    toggle(coords.translate(-1, 1), CollisionFlag.WALL_SOUTH_EAST_PROJ_BLOCKER, add)
                }
            }
            1 -> {
                toggle(coords, CollisionFlag.WALL_NORTH_EAST, add)
                toggle(coords.translate(1, 1), CollisionFlag.WALL_SOUTH_WEST, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.WALL_NORTH_EAST_PROJ_BLOCKER, add)
                    toggle(coords.translate(1, 1), CollisionFlag.WALL_SOUTH_WEST_PROJ_BLOCKER, add)
                }
            }
            2 -> {
                toggle(coords, CollisionFlag.WALL_SOUTH_EAST, add)
                toggle(coords.translate(1, -1), CollisionFlag.WALL_NORTH_WEST, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.WALL_SOUTH_EAST_PROJ_BLOCKER, add)
                    toggle(coords.translate(1, -1), CollisionFlag.WALL_NORTH_WEST_PROJ_BLOCKER, add)
                }
            }
            3 -> {
                toggle(coords, CollisionFlag.WALL_SOUTH_WEST, add)
                toggle(coords.translate(-1, -1), CollisionFlag.WALL_NORTH_EAST, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.WALL_SOUTH_WEST_PROJ_BLOCKER, add)
                    toggle(
                        coords.translate(-1, -1),
                        CollisionFlag.WALL_NORTH_EAST_PROJ_BLOCKER,
                        add,
                    )
                }
            }
        }
    } else if (shape == LocShapes.WALL_L) {
        when (angle) {
            0 -> {
                toggle(coords, CollisionFlag.WALL_WEST or CollisionFlag.WALL_NORTH, add)
                toggle(coords.translate(-1, 0), CollisionFlag.WALL_EAST, add)
                toggle(coords.translate(0, 1), CollisionFlag.WALL_SOUTH, add)
                if (blockRange) {
                    val flag =
                        CollisionFlag.WALL_WEST_PROJ_BLOCKER or
                            CollisionFlag.WALL_NORTH_PROJ_BLOCKER
                    toggle(coords, flag, add)
                    toggle(coords.translate(-1, 0), CollisionFlag.WALL_EAST_PROJ_BLOCKER, add)
                    toggle(coords.translate(0, 1), CollisionFlag.WALL_SOUTH_PROJ_BLOCKER, add)
                }
            }
            1 -> {
                toggle(coords, CollisionFlag.WALL_NORTH or CollisionFlag.WALL_EAST, add)
                toggle(coords.translate(0, 1), CollisionFlag.WALL_SOUTH, add)
                toggle(coords.translate(1, 0), CollisionFlag.WALL_WEST, add)
                if (blockRange) {
                    val flag =
                        CollisionFlag.WALL_NORTH_PROJ_BLOCKER or
                            CollisionFlag.WALL_EAST_PROJ_BLOCKER
                    toggle(coords, flag, add)
                    toggle(coords.translate(0, 1), CollisionFlag.WALL_SOUTH_PROJ_BLOCKER, add)
                    toggle(coords.translate(1, 0), CollisionFlag.WALL_WEST_PROJ_BLOCKER, add)
                }
            }
            2 -> {
                toggle(coords, CollisionFlag.WALL_EAST or CollisionFlag.WALL_SOUTH, add)
                toggle(coords.translate(1, 0), CollisionFlag.WALL_WEST, add)
                toggle(coords.translate(0, -1), CollisionFlag.WALL_NORTH, add)
                if (blockRange) {
                    val flag =
                        CollisionFlag.WALL_EAST_PROJ_BLOCKER or
                            CollisionFlag.WALL_SOUTH_PROJ_BLOCKER
                    toggle(coords, flag, add)
                    toggle(coords.translate(1, 0), CollisionFlag.WALL_WEST_PROJ_BLOCKER, add)
                    toggle(coords.translate(0, -1), CollisionFlag.WALL_NORTH_PROJ_BLOCKER, add)
                }
            }
            3 -> {
                toggle(coords, CollisionFlag.WALL_SOUTH or CollisionFlag.WALL_WEST, add)
                toggle(coords.translate(0, -1), CollisionFlag.WALL_NORTH, add)
                toggle(coords.translate(-1, 0), CollisionFlag.WALL_EAST, add)
                if (blockRange) {
                    val flag =
                        CollisionFlag.WALL_SOUTH_PROJ_BLOCKER or
                            CollisionFlag.WALL_WEST_PROJ_BLOCKER
                    toggle(coords, flag, add)
                    toggle(coords.translate(0, -1), CollisionFlag.WALL_NORTH_PROJ_BLOCKER, add)
                    toggle(coords.translate(-1, 0), CollisionFlag.WALL_EAST_PROJ_BLOCKER, add)
                }
            }
        }
    }
}

private fun CollisionFlagMap.toggleGroundDecor(coords: CoordGrid, add: Boolean) {
    toggle(coords, CollisionFlag.GROUND_DECOR, add)
}

private fun CollisionFlagMap.toggle(coords: CoordGrid, mask: Int, add: Boolean) {
    if (add) {
        add(coords.x, coords.z, coords.level, mask)
    } else {
        remove(coords.x, coords.z, coords.level, mask)
    }
}
