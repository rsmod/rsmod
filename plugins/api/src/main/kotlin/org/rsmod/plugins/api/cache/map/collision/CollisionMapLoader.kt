package org.rsmod.plugins.api.cache.map.collision

import com.google.inject.Inject
import io.guthix.buffer.readIncrSmallSmart
import io.guthix.buffer.readUnsignedSmallSmart
import io.guthix.js5.Js5Archive
import io.netty.buffer.ByteBuf
import org.rsmod.game.cache.GameCache
import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.model.obj.GameObjectMap
import org.rsmod.game.model.domain.repo.XteaRepository
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.map.MapSquare
import org.rsmod.game.model.obj.GameObject
import org.rsmod.game.model.obj.type.ObjectTypeList
import org.rsmod.pathfinder.flag.CollisionFlag

private const val MAPS_ARCHIVE = 5
private const val MAP_CONTENTS_FILE = 0
private const val HIGHEST_LEVEL = 4
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
        val map = cache.file(archive, "m$name", MAP_CONTENTS_FILE)
        val loc = cache.file(archive, "l$name", MAP_CONTENTS_FILE, keys)
        loadCollision(map, loc)
    }

    private fun MapSquare.loadCollision(map: ByteBuf, loc: ByteBuf) {
        val floorMask = mutableMapOf<Coordinates, Int>()
        for (level in 0 until HIGHEST_LEVEL) {
            for (x in 0 until MapSquare.SIZE) {
                for (y in 0 until MapSquare.SIZE) {
                    while (map.isReadable) {
                        val opcode = map.readUnsignedByte().toInt()
                        if (opcode == 0) {
                            break
                        }

                        if (opcode == 1) {
                            map.readByte()
                            break
                        }

                        if (opcode <= 49) {
                            map.readByte()
                        } else if (opcode <= 89) {
                            val localCoords = Coordinates(x, y, level)
                            floorMask[localCoords] = (opcode - 49)
                        }
                    }
                }
            }
        }

        for (level in 0 until HIGHEST_LEVEL) {
            for (x in 0 until MapSquare.SIZE) {
                for (y in 0 until MapSquare.SIZE) {
                    val localCoords = Coordinates(x, y, level)
                    val localMask = floorMask[localCoords] ?: 0
                    if ((localMask and BLOCKED_TILE_BIT) != BLOCKED_TILE_BIT) {
                        continue
                    }
                    var endLevel = level
                    val bridgeCoords = Coordinates(x, y, 1)
                    val bridgeMask = floorMask[bridgeCoords] ?: 0
                    if ((bridgeMask and BRIDGE_TILE_BIT) == BRIDGE_TILE_BIT) {
                        endLevel--
                    }
                    if (endLevel >= 0) {
                        val coords = coords(level).translate(x, y)
                        collisionMap.add(coords, CollisionFlag.FLOOR)
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
                val shape = attributes shr 2
                val rotation = attributes and 0x3
                var level = (packed shr 12) and 0x3

                val localCoords = Coordinates(localX, localY, 1)
                val floor = floorMask[localCoords] ?: 0
                if ((floor and BRIDGE_TILE_BIT) == BRIDGE_TILE_BIT) {
                    level--
                }

                if (level >= 0) {
                    val type = objTypes[objectId]
                    val coords = coords(level).translate(localX, localY)
                    val obj = GameObject(type, coords, shape, rotation)
                    collisionMap.addObject(obj)
                    objMap.addStatic(obj)
                }
            }
        }
    }
}
