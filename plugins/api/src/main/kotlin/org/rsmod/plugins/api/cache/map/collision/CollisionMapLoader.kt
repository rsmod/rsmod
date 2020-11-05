package org.rsmod.plugins.api.cache.map.collision

import com.google.inject.Inject
import io.guthix.buffer.readIncrSmallSmart
import io.guthix.buffer.readUnsignedSmallSmart
import io.guthix.js5.Js5Archive
import io.netty.buffer.ByteBuf
import org.rsmod.game.cache.GameCache
import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.collision.GameObjectMap
import org.rsmod.game.model.domain.repo.XteaRepository
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.map.MapSquare
import org.rsmod.game.model.obj.GameObject
import org.rsmod.game.model.obj.type.ObjectTypeList

private const val MAPS_ARCHIVE = 5
private const val HIGHEST_PLANE = 4
private const val BLOCKED_TILE_BIT = 0x1
private const val BRIDGE_TILE_BIT = 0x2

class CollisionMapLoader @Inject constructor(
    private val cache: GameCache,
    private val collisionMap: CollisionMap,
    private val objMap: GameObjectMap,
    private val objTypes: ObjectTypeList,
    private val xteas: XteaRepository
) {

    fun load() {
        val archive = cache.archive(MAPS_ARCHIVE)
        val xteas = xteas.entries()
        xteas.forEach { (mapSquare, keys) ->
            MapSquare(mapSquare).load(archive, keys)
        }
    }

    private fun MapSquare.load(archive: Js5Archive, keys: IntArray) {
        val name = "${x}_$y"
        val map = cache.singleFile(archive, "m$name")
        val loc = cache.singleFile(archive, "l$name", keys)
        loadCollision(map, loc)
    }

    private fun MapSquare.loadCollision(map: ByteBuf, loc: ByteBuf) {
        val floorDecor = mutableMapOf<Coordinates, Int>()
        for (plane in 0 until HIGHEST_PLANE) {
            for (x in 0 until MapSquare.SIZE) {
                for (y in 0 until MapSquare.SIZE) {
                    while (map.isReadable) {
                        val flag = map.readUnsignedByte().toInt()
                        if (flag == 0) {
                            break
                        }

                        if (flag == 1) {
                            map.readByte()
                            break
                        }

                        if (flag <= 49) {
                            map.readByte()
                        } else if (flag <= 81) {
                            val localCoords = Coordinates(x, y, plane)
                            floorDecor[localCoords] = (flag - 81)
                        }
                    }
                }
            }
        }

        for (plane in 0 until HIGHEST_PLANE) {
            for (x in 0 until MapSquare.SIZE) {
                for (y in 0 until MapSquare.SIZE) {
                    val localCoords = Coordinates(x, y, plane)
                    val floor = floorDecor[localCoords] ?: 0
                    if ((floor and BLOCKED_TILE_BIT) != BLOCKED_TILE_BIT) {
                        continue
                    }
                    var endPlane = plane
                    if ((floor and BRIDGE_TILE_BIT) == BRIDGE_TILE_BIT) {
                        endPlane--
                    }
                    if (endPlane >= 0) {
                        val coords = coords(plane).translate(x, y)
                        collisionMap.addFloorDecor(coords)
                    }
                }
            }
        }

        var objectId = -1
        while (loc.isReadable) {
            val offset = loc.readIncrSmallSmart()
            if (offset == 0) {
                return
            }
            var packed = 0
            objectId += offset
            while (loc.isReadable) {
                val diff = loc.readUnsignedSmallSmart()
                if (diff == 0) {
                    break
                }
                packed += diff - 1
                val attributes = loc.readUnsignedByte().toInt()
                val localX = (packed shr 6) and 0x3F
                val localY = packed and 0x3F
                if (localX !in 0 until MapSquare.SIZE || localY !in 0 until MapSquare.SIZE) {
                    continue
                }
                val slot = attributes shr 2
                val rotation = attributes and 0x3
                var plane = (packed shr 12) and 0x3

                val localCoords = Coordinates(localX, localY, plane)
                val floor = floorDecor[localCoords] ?: 0
                if ((floor and BRIDGE_TILE_BIT) == BRIDGE_TILE_BIT) {
                    plane--
                }

                if (plane >= 0) {
                    val type = objTypes[objectId]
                    val coords = coords(plane).translate(localX, localY)
                    val obj = GameObject(type, coords, slot, rotation)
                    collisionMap.addObject(obj)
                    objMap.addStatic(obj)
                }
            }
        }
    }
}
