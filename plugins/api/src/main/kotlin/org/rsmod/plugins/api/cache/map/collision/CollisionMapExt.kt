package org.rsmod.plugins.api.cache.map.collision

import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.obj.GameObject
import org.rsmod.pathfinder.flag.CollisionFlag
import org.rsmod.plugins.api.model.obj.ObjectShape

fun CollisionMap.addObject(obj: GameObject) {
    changeObject(obj, add = true)
}

fun CollisionMap.removeObject(obj: GameObject) {
    changeObject(obj, add = false)
}

internal fun CollisionMap.addFloorDecor(coords: Coordinates) {
    changeFloorDecor(coords, add = true)
}

private fun CollisionMap.changeObject(obj: GameObject, add: Boolean) {
    val type = obj.type
    val shape = obj.shape
    val coords = obj.coords
    val rotation = obj.rotation
    val clipType = type.clipType
    val blockPath = type.blockPath
    val blockProjectile = type.blockProjectile

    if (shape in ObjectShape.WALL_SHAPES && clipType != 0) {
        changeWall(coords, rotation, shape, blockProjectile, add)
    } else if (shape in ObjectShape.NORMAL_SHAPES && clipType != 0) {
        var width = type.width
        var length = type.length
        if (rotation == 1 || rotation == 3) {
            width = type.length
            length = type.width
        }
        changeNormal(coords, width, length, blockPath, blockProjectile, add)
    } else if (shape in ObjectShape.GROUND_DECOR_SHAPES && clipType == 1) {
        changeFloorDecor(coords, add)
    }
}

private fun CollisionMap.changeNormal(
    coords: Coordinates,
    width: Int, length: Int,
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

private fun CollisionMap.changeWall(
    coords: Coordinates,
    rotation: Int,
    shape: Int,
    blockProjectile: Boolean,
    add: Boolean
) {
    if (shape == 0) {
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
    } else if (shape == 1 || shape == 3) {
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
    } else if (shape == 2) {
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

private fun CollisionMap.changeFloorDecor(coords: Coordinates, add: Boolean) {
    change(coords, CollisionFlag.FLOOR_DECORATION, add)
}

private fun CollisionMap.change(coords: Coordinates, mask: Int, add: Boolean) {
    if (add) {
        add(coords, mask)
    } else {
        remove(coords, mask)
    }
}
