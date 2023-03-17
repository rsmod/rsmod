package org.rsmod.plugins.api.map.collision

import org.rsmod.game.map.Coordinates
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.CollisionFlag
import org.rsmod.plugins.api.map.GameObject
import org.rsmod.plugins.api.map.ObjectShape
import org.rsmod.plugins.api.map.ObjectSlot

public fun CollisionFlagMap.get(coords: Coordinates): Int {
    return get(coords.x, coords.z, coords.level)
}

public fun CollisionFlagMap.addObject(obj: GameObject) {
    changeObject(obj, add = true)
}

public fun CollisionFlagMap.removeObject(obj: GameObject) {
    changeObject(obj, add = false)
}

private fun CollisionFlagMap.changeObject(obj: GameObject, add: Boolean) {
    val shape = obj.shape() ?: error("Could not convert object shape. (id=${obj.shape})")
    val slot = obj.slot()
    val type = obj.type
    val coords = obj.coords
    val rotation = obj.rot
    val clipType = type.clipType
    val blockPath = type.blockPath
    val blockProjectile = type.blockProjectile

    if (slot == ObjectSlot.Wall && clipType != 0) {
        changeWall(coords, rotation, shape, blockProjectile, add)
    } else if (slot == ObjectSlot.Main && clipType != 0) {
        var width = type.width
        var length = type.height
        if (rotation == 1 || rotation == 3) {
            width = type.height
            length = type.width
        }
        changeNormal(coords, width, length, blockPath, blockProjectile, add)
    } else if (slot == ObjectSlot.GroundDetail && clipType == 1) {
        changeFloorDecor(coords, add)
    }
}

private fun CollisionFlagMap.changeNormal(
    coords: Coordinates,
    width: Int,
    length: Int,
    blockPath: Boolean,
    blockProjectile: Boolean,
    add: Boolean
) {
    for (x in 0 until width) {
        for (y in 0 until length) {
            val translate = coords.translate(x, y)
            change(translate, CollisionFlag.OBJECT, add)
            if (blockProjectile) {
                change(translate, CollisionFlag.OBJECT_PROJECTILE_BLOCKER, add)
            }
            if (blockPath) {
                change(translate, CollisionFlag.OBJECT_ROUTE_BLOCKER, add)
            }
        }
    }
}

private fun CollisionFlagMap.changeWall(
    coords: Coordinates,
    rotation: Int,
    shape: ObjectShape,
    blockProjectile: Boolean,
    add: Boolean
) {
    if (shape == ObjectShape.WallStraight) {
        when (rotation) {
            0 -> {
                change(coords, CollisionFlag.WALL_WEST, add)
                change(coords.translate(-1, 0), CollisionFlag.WALL_EAST, add)
                if (blockProjectile) {
                    change(coords, CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER, add)
                    change(coords.translate(-1, 0), CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER, add)
                }
            }
            1 -> {
                change(coords, CollisionFlag.WALL_NORTH, add)
                change(coords.translate(0, 1), CollisionFlag.WALL_SOUTH, add)
                if (blockProjectile) {
                    change(coords, CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER, add)
                    change(coords.translate(0, 1), CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER, add)
                }
            }
            2 -> {
                change(coords, CollisionFlag.WALL_EAST, add)
                change(coords.translate(1, 0), CollisionFlag.WALL_WEST, add)
                if (blockProjectile) {
                    change(coords, CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER, add)
                    change(coords.translate(1, 0), CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER, add)
                }
            }
            3 -> {
                change(coords, CollisionFlag.WALL_SOUTH, add)
                change(coords.translate(0, -1), CollisionFlag.WALL_NORTH, add)
                if (blockProjectile) {
                    change(coords, CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER, add)
                    change(coords.translate(0, -1), CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER, add)
                }
            }
        }
    } else if (shape == ObjectShape.WallDiagonalCorner || shape == ObjectShape.WallSquareCorner) {
        when (rotation) {
            0 -> {
                change(coords, CollisionFlag.WALL_NORTH_WEST, add)
                change(coords.translate(-1, 1), CollisionFlag.WALL_SOUTH_EAST, add)
                if (blockProjectile) {
                    change(coords, CollisionFlag.WALL_NORTH_WEST_PROJECTILE_BLOCKER, add)
                    change(coords.translate(-1, 1), CollisionFlag.WALL_SOUTH_EAST_PROJECTILE_BLOCKER, add)
                }
            }
            1 -> {
                change(coords, CollisionFlag.WALL_NORTH_EAST, add)
                change(coords.translate(1, 1), CollisionFlag.WALL_SOUTH_WEST, add)
                if (blockProjectile) {
                    change(coords, CollisionFlag.WALL_NORTH_EAST_PROJECTILE_BLOCKER, add)
                    change(coords.translate(1, 1), CollisionFlag.WALL_SOUTH_WEST_PROJECTILE_BLOCKER, add)
                }
            }
            2 -> {
                change(coords, CollisionFlag.WALL_SOUTH_EAST, add)
                change(coords.translate(1, -1), CollisionFlag.WALL_NORTH_WEST, add)
                if (blockProjectile) {
                    change(coords, CollisionFlag.WALL_SOUTH_EAST_PROJECTILE_BLOCKER, add)
                    change(coords.translate(1, -1), CollisionFlag.WALL_NORTH_WEST_PROJECTILE_BLOCKER, add)
                }
            }
            3 -> {
                change(coords, CollisionFlag.WALL_SOUTH_WEST, add)
                change(coords.translate(-1, -1), CollisionFlag.WALL_NORTH_EAST, add)
                if (blockProjectile) {
                    change(coords, CollisionFlag.WALL_SOUTH_WEST_PROJECTILE_BLOCKER, add)
                    change(coords.translate(-1, -1), CollisionFlag.WALL_NORTH_EAST_PROJECTILE_BLOCKER, add)
                }
            }
        }
    } else if (shape == ObjectShape.WallL) {
        when (rotation) {
            0 -> {
                change(coords, CollisionFlag.WALL_WEST or CollisionFlag.WALL_NORTH, add)
                change(coords.translate(-1, 0), CollisionFlag.WALL_EAST, add)
                change(coords.translate(0, 1), CollisionFlag.WALL_SOUTH, add)
                if (blockProjectile) {
                    val flag = CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER or
                        CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER
                    change(coords, flag, add)
                    change(coords.translate(-1, 0), CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER, add)
                    change(coords.translate(0, 1), CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER, add)
                }
            }
            1 -> {
                change(coords, CollisionFlag.WALL_NORTH or CollisionFlag.WALL_EAST, add)
                change(coords.translate(0, 1), CollisionFlag.WALL_SOUTH, add)
                change(coords.translate(1, 0), CollisionFlag.WALL_WEST, add)
                if (blockProjectile) {
                    val flag = CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER or
                        CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER
                    change(coords, flag, add)
                    change(coords.translate(0, 1), CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER, add)
                    change(coords.translate(1, 0), CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER, add)
                }
            }
            2 -> {
                change(coords, CollisionFlag.WALL_EAST or CollisionFlag.WALL_SOUTH, add)
                change(coords.translate(1, 0), CollisionFlag.WALL_WEST, add)
                change(coords.translate(0, -1), CollisionFlag.WALL_NORTH, add)
                if (blockProjectile) {
                    val flag = CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER or
                        CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER
                    change(coords, flag, add)
                    change(coords.translate(1, 0), CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER, add)
                    change(coords.translate(0, -1), CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER, add)
                }
            }
            3 -> {
                change(coords, CollisionFlag.WALL_SOUTH or CollisionFlag.WALL_WEST, add)
                change(coords.translate(0, -1), CollisionFlag.WALL_NORTH, add)
                change(coords.translate(-1, 0), CollisionFlag.WALL_EAST, add)
                if (blockProjectile) {
                    val flag = CollisionFlag.WALL_SOUTH_PROJECTILE_BLOCKER or
                        CollisionFlag.WALL_WEST_PROJECTILE_BLOCKER
                    change(coords, flag, add)
                    change(coords.translate(0, -1), CollisionFlag.WALL_NORTH_PROJECTILE_BLOCKER, add)
                    change(coords.translate(-1, 0), CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER, add)
                }
            }
        }
    }
}

private fun CollisionFlagMap.changeFloorDecor(coords: Coordinates, add: Boolean) {
    change(coords, CollisionFlag.FLOOR_DECORATION, add)
}

private fun CollisionFlagMap.change(coords: Coordinates, mask: Int, add: Boolean) {
    if (add) {
        add(coords.x, coords.z, coords.level, mask)
    } else {
        remove(coords.x, coords.z, coords.level, mask)
    }
}
