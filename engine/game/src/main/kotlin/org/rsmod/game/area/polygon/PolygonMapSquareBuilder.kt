package org.rsmod.game.area.polygon

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet
import java.util.BitSet
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.util.LocalMapSquareZone
import org.rsmod.map.zone.ZoneGrid

@DslMarker private annotation class PolygonAreaBuilderDsl

/**
 * Builder for registering polygon-based area data within a single 64x64 map square.
 *
 * ## Usage
 * Call [polygon] to define the corners of an area. Vertices do not need to be adjacent; edge lines
 * are automatically interpolated.
 *
 * For example, `vertex(0, 0)` followed by `vertex(3, 0)` is valid and preferred. By contrast,
 * `vertex(0, 0)`, `vertex(1, 0)`, and `vertex(3, 0)` will result in redundant workload.
 *
 * _Successive calls to [polygon] with the same `area` id on a single builder will be `OR`'d
 * together, forming a union of all the sub-polygons._
 *
 * ## Guarantees
 * - Implicitly closes the polygon if not already closed.
 * - Fills the interior using a scanline fill algorithm.
 * - Promotes area coverage to:
 *     - Zone-level if an entire 8x8 zone is covered.
 *     - Map-square-level if all 64x64 tiles across all four levels are covered.
 * - To benefit from map square optimization, all four levels should be specified when defining a
 *   full 64x64 polygon - assuming the area applies to all levels.
 *
 * ## Limitations
 * This builder only handles one map square. If a polygon spans multiple map squares, the caller
 * must split the polygon, create a builder per map square, and aggregate the results.
 */
// TODO: Add benchmarks so future optimizations can be compared properly.
@PolygonAreaBuilderDsl
public class PolygonMapSquareBuilder {
    private val coordAreas = Short2ObjectOpenHashMap<BitSet>()

    public fun polygon(area: Short, levels: Iterable<Int>, init: PolygonBuilder.() -> Unit) {
        val builder = PolygonBuilder(area = area, levels = levels, coordAreas = coordAreas)
        builder.apply(init)
        builder.close()
    }

    public fun build(): PolygonMapSquare {
        val zoneAreas = Short2ObjectOpenHashMap<BitSet>()
        val mapSquareAreas = ShortOpenHashSet()

        val levelCount = CoordGrid.LEVEL_COUNT
        val zoneTileLength = ZoneGrid.LENGTH
        val mapSquareZoneLength = LocalMapSquareZone.LENGTH

        val coordIterator = coordAreas.short2ObjectEntrySet().iterator()
        while (coordIterator.hasNext()) {
            val (area, bitset) = coordIterator.next()

            val isFullMapSquare = bitset.cardinality() == TOTAL_MAP_SQUARE_TILE_COUNT
            if (isFullMapSquare) {
                mapSquareAreas.add(area)
                coordIterator.remove()
                continue
            }

            for (level in 0 until levelCount) {
                for (tileX in 0 until zoneTileLength) {
                    for (tileZ in 0 until zoneTileLength) {
                        val baseX = tileX * mapSquareZoneLength
                        val baseZ = tileZ * mapSquareZoneLength
                        val isFullZone =
                            (0 until mapSquareZoneLength).all { dx ->
                                (0 until mapSquareZoneLength).all { dz ->
                                    val grid = MapSquareGrid(baseX + dx, baseZ + dz, level)
                                    bitset.get(grid.packed)
                                }
                            }
                        if (isFullZone) {
                            val zoneBitSet = zoneAreas.getOrPut(area) { BitSet() }
                            val localZone = LocalMapSquareZone(tileX, tileZ, level)
                            zoneBitSet.set(localZone.packed)
                            for (dx in 0 until mapSquareZoneLength) {
                                for (dz in 0 until mapSquareZoneLength) {
                                    val grid = MapSquareGrid(baseX + dx, baseZ + dz, level)
                                    bitset.clear(grid.packed)
                                }
                            }
                        }
                    }
                }
            }

            if (bitset.isEmpty) {
                coordIterator.remove()
            }
        }

        return PolygonMapSquare(
            coordAreas = coordAreas,
            zoneAreas = zoneAreas,
            mapSquareAreas = mapSquareAreas,
        )
    }

    @DslMarker private annotation class PolygonDsl

    @PolygonDsl
    public class PolygonBuilder(
        public val area: Short,
        levels: Iterable<Int>,
        private val coordAreas: Short2ObjectOpenHashMap<BitSet>,
    ) {
        private val levelList = levels.distinct().sorted()
        private val vertices = IntArrayList()
        private var anchor = MapSquareGrid.NULL

        private var lastVertex = MapSquareGrid.NULL
        private var lastDeltaSignX = Int.MIN_VALUE
        private var lastDeltaSignZ = Int.MIN_VALUE

        init {
            require(levelList.isNotEmpty()) { "List of levels must not be empty." }

            val validLevels = levelList.all { it in 0 until CoordGrid.LEVEL_COUNT }
            require(validLevels) { "Invalid level(s) specified: $levelList" }
        }

        private fun anchor(anchor: MapSquareGrid) {
            this.anchor = anchor
            this.lastVertex = anchor
        }

        public fun vertex(tileX: Int, tileZ: Int) {
            val tile = MapSquareGrid(tileX, tileZ)
            vertex(tile)
        }

        public fun vertex(tile: MapSquareGrid) {
            if (anchor == MapSquareGrid.NULL) {
                anchor(tile)
                return
            }
            connect(lastVertex, tile)
        }

        private fun connect(from: MapSquareGrid, to: MapSquareGrid) {
            val dx = to.x - from.x
            val dz = to.z - from.z
            val sx = dx.sign
            val sz = dz.sign

            val straightOr45 = (sx == 0 || sz == 0) || (abs(dx) == abs(dz))
            if (straightOr45 && sx == lastDeltaSignX && sz == lastDeltaSignZ) {
                replaceLastVertex(to)
            } else {
                vertices.add(to.packed)
            }

            if (straightOr45) {
                lastDeltaSignX = sx
                lastDeltaSignZ = sz
            } else {
                lastDeltaSignX = Int.MIN_VALUE
                lastDeltaSignZ = Int.MIN_VALUE
            }

            lastVertex = to
        }

        private fun replaceLastVertex(newVertex: MapSquareGrid) {
            check(vertices.isNotEmpty())
            vertices.removeInt(vertices.lastIndex)
            vertices.add(newVertex.packed)
            lastVertex = newVertex
        }

        internal fun close() {
            check(anchor != MapSquareGrid.NULL) { "No vertices defined for polygon (area=$area)" }
            val connectLastVertex = vertices.size > 1 && lastVertex != anchor
            if (connectLastVertex) {
                connect(from = lastVertex, to = anchor)
            }

            val singleTilePolygon = vertices.isEmpty()
            if (singleTilePolygon) {
                closeSingleTilePolygon(anchor)
                return
            }

            // Optimizes cases where the area covers the full map square (64x64x4 tiles).
            if (isFullMapSquare()) {
                replaceWithFullMapSquare()
                return
            }

            replaceWithFilledPolygon()
        }

        private fun closeSingleTilePolygon(tile: MapSquareGrid) {
            vertices.add(tile.packed) // Not required, but maintains logical consistency.
            val bitSet = coordAreas.getOrPut(area) { BitSet() }
            for (level in levelList) {
                val grid = MapSquareGrid(tile.x, tile.z, level)
                bitSet.set(grid.packed)
            }
        }

        private fun isFullMapSquare(): Boolean {
            if (vertices.size != 4) {
                return false
            }

            val allLevels = levelList.size == CoordGrid.LEVEL_COUNT
            if (!allLevels) {
                return false
            }

            val vertexList =
                intArrayOf(
                    vertices.getInt(0),
                    vertices.getInt(1),
                    vertices.getInt(2),
                    vertices.getInt(3),
                )

            var corner1 = false
            var corner2 = false
            var corner3 = false
            var corner4 = false

            for (vertex in vertexList) {
                val grid = MapSquareGrid(vertex)
                when {
                    grid.x == 0 && grid.z == 0 -> corner1 = true
                    grid.x == 63 && grid.z == 0 -> corner2 = true
                    grid.x == 63 && grid.z == 63 -> corner3 = true
                    grid.x == 0 && grid.z == 63 -> corner4 = true
                    else -> return false
                }
            }

            return corner1 && corner2 && corner3 && corner4
        }

        private fun replaceWithFullMapSquare() {
            val bitSet = coordAreas.getOrPut(area) { BitSet(TOTAL_MAP_SQUARE_TILE_COUNT) }
            bitSet.clear()
            bitSet.set(0, TOTAL_MAP_SQUARE_TILE_COUNT)
        }

        private fun replaceWithFilledPolygon() {
            val filled = BitSet()
            /*
             * Note: this "one-vertex" line check works because the current system collapses any
             * series of collinear intermediate vertices along a straight line into a single
             * endpoint. If `replaceLastVertex` is ever removed or altered, you must reintroduce
             * equivalent sanitization (e.g., remove the last vertex inserted in the straight
             * segment) so that `vertices.size == 1` continues to reliably indicate a single
             * straight line.
             */
            val isLinePolygon = vertices.size == 1
            if (isLinePolygon) {
                val endVertex = MapSquareGrid(vertices.last())
                fillLine(filled, anchor, endVertex)
            } else {
                fillPolygon(filled)
            }

            val bitSet = coordAreas.getOrPut(area) { BitSet() }
            bitSet.or(filled)
        }

        private fun fillLine(filled: BitSet, start: MapSquareGrid, end: MapSquareGrid) {
            val deltaX = end.x - start.x
            val deltaZ = end.z - start.z
            when {
                deltaZ == 0 -> fillHorizontalLine(filled, start, end)
                deltaX == 0 -> fillVerticalLine(filled, start, end)
                else -> fillBresenhamLine(filled, start, end)
            }
        }

        private fun fillHorizontalLine(filled: BitSet, start: MapSquareGrid, end: MapSquareGrid) {
            require(start.z == end.z) { "Horizontal lines must be specified on the same z-axis." }
            require(start.x != end.x) { "Horizontal lines must not be on the same x-coord." }
            val minX = min(start.x, end.x)
            val maxX = max(start.x, end.x)
            for (level in levelList) {
                for (x in minX..maxX) {
                    val grid = MapSquareGrid(x, start.z, level)
                    filled.set(grid.packed)
                }
            }
        }

        private fun fillVerticalLine(filled: BitSet, start: MapSquareGrid, end: MapSquareGrid) {
            require(start.x == end.x) { "Vertical lines must be specified on the same x-axis." }
            require(start.z != end.z) { "Vertical lines must not be on the same z-coord." }
            val minZ = min(start.z, end.z)
            val maxZ = max(start.z, end.z)
            for (level in levelList) {
                for (z in minZ..maxZ) {
                    val grid = MapSquareGrid(start.x, z, level)
                    filled.set(grid.packed)
                }
            }
        }

        private fun fillBresenhamLine(filled: BitSet, start: MapSquareGrid, end: MapSquareGrid) {
            val dx = abs(end.x - start.x)
            val dz = abs(end.z - start.z)
            val sx = (end.x - start.x).sign
            val sz = (end.z - start.z).sign
            var x = start.x
            var z = start.z
            var err = dx - dz
            while (true) {
                for (level in levelList) {
                    val grid = MapSquareGrid(x, z, level)
                    filled.set(grid.packed)
                }
                if (x == end.x && z == end.z) {
                    break
                }
                val e2 = err * 2
                if (e2 > -dz) {
                    err -= dz
                    x += sx
                }
                if (e2 < dx) {
                    err += dx
                    z += sz
                }
            }
        }

        // Uses Active Edge Table with the non-zero winding rule to fill the polygon.
        private fun fillPolygon(filled: BitSet) {
            val buckets = Array(MapSquareGrid.LENGTH) { mutableListOf<Edge>() }
            val edges = mutableListOf<Edge>()

            val vertexCount = vertices.size
            val startVertex = anchor.packed
            for (i in 0 until vertexCount) {
                val packed1 = if (i == 0) startVertex else vertices.getInt(i - 1)
                val packed2 = vertices.getInt(i)

                val grid1 = MapSquareGrid(packed1)
                val x1 = grid1.x
                val z1 = grid1.z

                val grid2 = MapSquareGrid(packed2)
                val x2 = grid2.x
                val z2 = grid2.z

                if (z1 == z2) {
                    continue
                }

                val zMin: Int
                val zMax: Int
                val x0: Float
                val wind: Int
                if (z1 < z2) {
                    zMin = z1
                    zMax = z2 - 1
                    x0 = x1.toFloat()
                    wind = 1
                } else {
                    zMin = z2
                    zMax = z1 - 1
                    x0 = x2.toFloat()
                    wind = -1
                }

                val invSlope = (x2 - x1).toFloat() / (z2 - z1)
                val edge = Edge(x0, invSlope, zMax, wind)
                buckets[zMin].add(edge)
            }

            for (z in 0 until MapSquareGrid.LENGTH) {
                edges += buckets[z]

                edges.sortBy { it.x }

                var winding = 0
                var prevX = 0f
                for (edge in edges) {
                    val cx = edge.x
                    if (winding != 0) {
                        val x0 = ceil(prevX).toInt()
                        val x1 = floor(cx).toInt()
                        for (level in levelList) {
                            for (x in x0..x1) {
                                val grid = MapSquareGrid(x, z, level)
                                filled.set(grid.packed)
                            }
                        }
                    }
                    winding += edge.winding
                    edge.x += edge.invSlope
                    prevX = cx
                }

                var write = 0
                for (edge in edges) {
                    if (edge.zMax != z) {
                        edges[write++] = edge
                    }
                }
                if (write < edges.size) {
                    val clearList = edges.subList(write, edges.size)
                    clearList.clear()
                }
            }

            // "Stroke" the outline segments so that every vertex tile specified via `vertex(...)`
            // is guaranteed to be filled, even if it spans only one scanline.
            for (i in 0 until vertexCount) {
                val prevPacked = if (i == 0) startVertex else vertices.getInt(i - 1)
                val prev = MapSquareGrid(prevPacked)
                val curr = MapSquareGrid(vertices.getInt(i))
                fillLine(filled, prev, curr)
            }
        }
    }

    private companion object {
        /** `64 * 64 * 4 = 16,384` */
        const val TOTAL_MAP_SQUARE_TILE_COUNT =
            MapSquareGrid.LENGTH * MapSquareGrid.LENGTH * CoordGrid.LEVEL_COUNT

        private data class Edge(var x: Float, val invSlope: Float, val zMax: Int, val winding: Int)
    }
}
