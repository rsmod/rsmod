package org.rsmod.api.cache.map.tile

import io.netty.buffer.ByteBuf
import org.rsmod.api.cache.map.MapDefinition
import org.rsmod.api.cache.map.SimpleMapDefinition
import org.rsmod.api.cache.util.InlineByteBuf
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid

public object MapTileDecoder {
    public fun decode(buf: InlineByteBuf): SimpleMapDefinition {
        var cursor = buf.newCursor()
        val def = SimpleMapDefinition()
        for (level in 0 until CoordGrid.LEVEL_COUNT) {
            for (x in 0 until MapSquareGrid.LENGTH) {
                for (z in 0 until MapSquareGrid.LENGTH) {
                    while (buf.isReadable(cursor)) {
                        cursor = buf.readShort(cursor)
                        val opcode = cursor.value
                        when {
                            opcode == 0 -> {
                                break
                            }
                            opcode == 1 -> {
                                cursor = buf.readByte(cursor)
                                break
                            }
                            opcode <= 49 -> {
                                cursor = buf.readShort(cursor)
                                val id = cursor.value.toShort().toInt()
                                if (id != 0) {
                                    def[x, z, level] = SimpleMapDefinition.COLOURED
                                }
                            }
                            opcode <= 81 -> {
                                val rule = (opcode - 49).toByte().toInt()
                                if ((rule and MapDefinition.BLOCK_MAP_SQUARE) != 0) {
                                    def[x, z, level] = SimpleMapDefinition.BLOCK_MAP_SQUARE
                                }
                                if ((rule and MapDefinition.LINK_BELOW) != 0) {
                                    def[x, z, level] = SimpleMapDefinition.LINK_BELOW
                                    if (level == 1) {
                                        def[x, z, 0] = SimpleMapDefinition.BRIDGE
                                    }
                                }
                                if ((rule and MapDefinition.REMOVE_ROOFS) != 0) {
                                    def[x, z, level] = SimpleMapDefinition.REMOVE_ROOFS
                                }
                            }
                            else -> {
                                val id = (opcode - 81).toShort().toInt()
                                if (id != 0) {
                                    def[x, z, level] = SimpleMapDefinition.COLOURED
                                }
                            }
                        }
                    }
                }
            }
        }
        return def
    }

    // This provides a more detailed view of all the configs per tile.
    public fun decode(buf: ByteBuf): MapDefinition {
        val tileHeights = hashMapOf<MapSquareGrid, Int>()
        val overlays = hashMapOf<MapSquareGrid, TileOverlay>()
        val underlays = hashMapOf<MapSquareGrid, TileUnderlay>()
        val rules = hashMapOf<MapSquareGrid, Byte>()
        for (level in 0 until CoordGrid.LEVEL_COUNT) {
            for (x in 0 until MapSquareGrid.LENGTH) {
                for (z in 0 until MapSquareGrid.LENGTH) {
                    while (buf.isReadable) {
                        val opcode = buf.readUnsignedShort()
                        when {
                            opcode == 0 -> {
                                val coords = MapSquareGrid(x, z, level)
                                tileHeights[coords] = Int.MIN_VALUE
                                break
                            }
                            opcode == 1 -> {
                                val coords = MapSquareGrid(x, z, level)
                                tileHeights[coords] = buf.readUnsignedByte().toInt()
                                break
                            }
                            opcode <= 49 -> {
                                val id = buf.readShort().toInt()
                                if (id != 0) {
                                    val path = ((opcode - 2) shr 2)
                                    val rot = ((opcode - 2) and 0x3)
                                    val coords = MapSquareGrid(x, z, level)
                                    overlays[coords] = TileOverlay((id - 1) and 0xFFFF, path, rot)
                                }
                            }
                            opcode <= 81 -> {
                                val coords = MapSquareGrid(x, z, level)
                                rules[coords] = (opcode - 49).toByte()
                            }
                            else -> {
                                val coords = MapSquareGrid(x, z, level)
                                val id = opcode - 81
                                underlays[coords] = TileUnderlay(id and 0xFF)
                            }
                        }
                    }
                }
            }
        }
        return MapDefinition(tileHeights, rules, overlays, underlays)
    }
}
