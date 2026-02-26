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

public fun CollisionFlagMap.isWalkBlocked(coords: CoordGrid): Boolean =
    this[coords] and CollisionFlag.WR_GRND != 0

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
            toggle(translate, CollisionFlag.WALK_SCENARY, add)
            if (blockRange) {
                toggle(translate, CollisionFlag.VIS_SCENARY, add)
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
                toggle(coords, CollisionFlag.W_W, add)
                toggle(coords.translate(-1, 0), CollisionFlag.W_E, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.V_W, add)
                    toggle(coords.translate(-1, 0), CollisionFlag.V_E, add)
                }
            }
            1 -> {
                toggle(coords, CollisionFlag.W_N, add)
                toggle(coords.translate(0, 1), CollisionFlag.W_S, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.V_N, add)
                    toggle(coords.translate(0, 1), CollisionFlag.V_S, add)
                }
            }
            2 -> {
                toggle(coords, CollisionFlag.W_E, add)
                toggle(coords.translate(1, 0), CollisionFlag.W_W, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.V_E, add)
                    toggle(coords.translate(1, 0), CollisionFlag.V_W, add)
                }
            }
            3 -> {
                toggle(coords, CollisionFlag.W_S, add)
                toggle(coords.translate(0, -1), CollisionFlag.W_N, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.V_S, add)
                    toggle(coords.translate(0, -1), CollisionFlag.V_N, add)
                }
            }
        }
    } else if (shape == LocShapes.WALL_DIAGONAL_CORNER || shape == LocShapes.WALL_SQUARE_CORNER) {
        when (angle) {
            0 -> {
                toggle(coords, CollisionFlag.W_NW, add)
                toggle(coords.translate(-1, 1), CollisionFlag.W_SE, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.V_NW, add)
                    toggle(coords.translate(-1, 1), CollisionFlag.V_SE, add)
                }
            }
            1 -> {
                toggle(coords, CollisionFlag.W_NE, add)
                toggle(coords.translate(1, 1), CollisionFlag.W_SW, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.V_NE, add)
                    toggle(coords.translate(1, 1), CollisionFlag.V_SW, add)
                }
            }
            2 -> {
                toggle(coords, CollisionFlag.W_SE, add)
                toggle(coords.translate(1, -1), CollisionFlag.W_NW, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.V_SE, add)
                    toggle(coords.translate(1, -1), CollisionFlag.V_NW, add)
                }
            }
            3 -> {
                toggle(coords, CollisionFlag.W_SW, add)
                toggle(coords.translate(-1, -1), CollisionFlag.W_NE, add)
                if (blockRange) {
                    toggle(coords, CollisionFlag.V_SW, add)
                    toggle(
                        coords.translate(-1, -1),
                        CollisionFlag.V_NE,
                        add,
                    )
                }
            }
        }
    } else if (shape == LocShapes.WALL_L) {
        when (angle) {
            0 -> {
                toggle(coords, CollisionFlag.W_W or CollisionFlag.W_N, add)
                toggle(coords.translate(-1, 0), CollisionFlag.W_E, add)
                toggle(coords.translate(0, 1), CollisionFlag.W_S, add)
                if (blockRange) {
                    val flag =
                        CollisionFlag.V_W or
                            CollisionFlag.V_N
                    toggle(coords, flag, add)
                    toggle(coords.translate(-1, 0), CollisionFlag.V_E, add)
                    toggle(coords.translate(0, 1), CollisionFlag.V_S, add)
                }
            }
            1 -> {
                toggle(coords, CollisionFlag.W_N or CollisionFlag.W_E, add)
                toggle(coords.translate(0, 1), CollisionFlag.W_S, add)
                toggle(coords.translate(1, 0), CollisionFlag.W_W, add)
                if (blockRange) {
                    val flag =
                        CollisionFlag.V_N or
                            CollisionFlag.V_E
                    toggle(coords, flag, add)
                    toggle(coords.translate(0, 1), CollisionFlag.V_S, add)
                    toggle(coords.translate(1, 0), CollisionFlag.V_W, add)
                }
            }
            2 -> {
                toggle(coords, CollisionFlag.W_E or CollisionFlag.W_S, add)
                toggle(coords.translate(1, 0), CollisionFlag.W_W, add)
                toggle(coords.translate(0, -1), CollisionFlag.W_N, add)
                if (blockRange) {
                    val flag =
                        CollisionFlag.V_E or
                            CollisionFlag.V_S
                    toggle(coords, flag, add)
                    toggle(coords.translate(1, 0), CollisionFlag.V_W, add)
                    toggle(coords.translate(0, -1), CollisionFlag.V_N, add)
                }
            }
            3 -> {
                toggle(coords, CollisionFlag.W_S or CollisionFlag.W_W, add)
                toggle(coords.translate(0, -1), CollisionFlag.W_N, add)
                toggle(coords.translate(-1, 0), CollisionFlag.W_E, add)
                if (blockRange) {
                    val flag =
                        CollisionFlag.V_S or
                            CollisionFlag.V_W
                    toggle(coords, flag, add)
                    toggle(coords.translate(0, -1), CollisionFlag.V_N, add)
                    toggle(coords.translate(-1, 0), CollisionFlag.V_E, add)
                }
            }
        }
    }
}

private fun CollisionFlagMap.toggleGroundDecor(coords: CoordGrid, add: Boolean) {
    toggle(coords, CollisionFlag.WR_GROUND_DECOR, add)
}

private fun CollisionFlagMap.toggle(coords: CoordGrid, mask: Int, add: Boolean) {
    if (add) {
        add(coords.x, coords.z, coords.level, mask)
    } else {
        remove(coords.x, coords.z, coords.level, mask)
    }
}
