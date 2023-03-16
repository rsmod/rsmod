package org.rsmod.plugins.api.cache.map.collision

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readUnsignedShortSmart
import org.openrs2.buffer.use
import org.openrs2.cache.Cache
import org.rsmod.game.map.Coordinates
import org.rsmod.game.map.square.MapSquare
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.api.cache.map.xtea.XteaRepository
import org.rsmod.plugins.api.cache.type.obj.ObjectTypeList
import org.rsmod.plugins.api.map.collision.CollisionMap
import javax.inject.Inject

public class CollisionMapLoader @Inject constructor(
    @GameCache private val cache: Cache,
    private val xteas: XteaRepository,
    private val objects: ObjectTypeList
) {

    public fun load(): CollisionMap {
        val flags = CollisionFlagMap()
        xteas.forEach { (mapSquare, key) ->
            val name = "${mapSquare.x}_${mapSquare.z}"
            cache.read(MAPS_ARCHIVE, "m$name", file = 0).use { map ->
                cache.read(MAPS_ARCHIVE, "l$name", file = 0, key).use { loc ->
                    loadFlags(flags, map, loc)
                }
            }
        }
        return CollisionMap(flags)
    }

    public companion object {

        private const val MAPS_ARCHIVE = 5

        private const val BLOCKED_TILE_BIT = 0x1
        private const val BRIDGE_TILE_BIT = 0x2

        public fun loadFlags(dest: CollisionFlagMap, map: ByteBuf, loc: ByteBuf) {
            for (level in 0 until Coordinates.LEVEL_COUNT) {
                for (x in 0 until MapSquare.SIZE) {
                    for (z in 0 until MapSquare.SIZE) {
                        val opcode = map.readUnsignedByte().toInt()
                        when {
                            opcode == 0 -> {
                                // tileHeights[level][x][y] = Int.MIN_VALUE
                                break
                            }
                            opcode == 1 -> {
                                // tileHeights[level][x][y] = map.readUnsignedByte().toInt()
                                break
                            }
                            opcode <= 49 -> {
                                val id = map.readByte() - 1
                                val path = ((opcode - 2) shr 2)
                                val rot = ((opcode - 2) and 0x3)
                                // overlays[level][x][y] = Overlay(id, path, rotation)
                            }
                            opcode <= 81 -> {
                                // renderRules[level][x][y] = (opcode - 49).toByte()
                            }
                            else -> {
                                // underlays[level][x][y] = Underlay((opcode - 81).toShort())
                            }
                        }
                    }
                }
            }

            var currObjectId = -1
            while (loc.isReadable) {
                val offset = loc.readIncrUnsignedShortSmart()
                if (offset == 0) break
                currObjectId += offset
                var localOffset = 0
                while (loc.isReadable) {
                    val diff = loc.readUnsignedShortSmart()
                    if (diff == 0) break
                    val attribs = loc.readUnsignedByte().toInt()
                    val shape = attribs shr 2
                    val rot = attribs and 0x3
                    localOffset += diff - 1
                    val localX = (localOffset shr 6) and 0x3F
                    val localY = localOffset and 0x3F
                    val level = (localOffset shr 12) and 0x3

                    // TODO: take bridge flags into account
                }
            }
        }

        private fun ByteBuf.readIncrUnsignedShortSmart(): Int {
            var value = 0
            var curr = readUnsignedShortSmart()
            while (curr == 0x7FFF) {
                value += curr
                curr = readUnsignedShortSmart()
            }
            value += curr
            return value
        }
    }
}
