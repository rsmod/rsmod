package org.rsmod.game.area.util

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareGrid
import org.rsmod.map.square.MapSquareKey

public object PolygonMapSquareClipper {
    /**
     * Calls [clip] after ensuring the input polygon is explicitly closed.
     *
     * If the first and last vertex in [vertices] are not equal, the first coordinate is appended to
     * close the polygon before clipping.
     *
     * @see [clip]
     */
    public fun closeAndClip(vertices: List<CoordGrid>): Map<MapSquareKey, List<CoordGrid>> {
        require(vertices.isNotEmpty()) { "List of vertices must not be empty." }
        val implicitClose = vertices.size > 1 && vertices.first() != vertices.last()
        val closed = if (implicitClose) vertices + vertices.first() else vertices
        return clip(closed)
    }

    /**
     * Given a list of polygon vertices, returns a map of clipped polygon segments, one per
     * intersected [MapSquareKey].
     *
     * This function is primarily used to convert multi-map-square polygons into a form that can be
     * processed by systems which only accept polygons fully contained within a single 64x64 map
     * square.
     *
     * The polygon is clipped using the Sutherland-Hodgman algorithm against the boundaries of each
     * relevant map square. For each intersected square, a list of vertices is returned representing
     * the clipped sub-polygon that lies entirely within it.
     *
     * If all input vertices lie within a single [MapSquareKey], the original [vertices] list is
     * returned under that key. The polygon is treated as closed - the first and last vertex will be
     * connected even if not explicitly closed in the input.
     *
     * @param vertices the polygon vertex coordinates as [CoordGrid]s.
     * @return map of [MapSquareKey] to clipped polygon segments within that square.
     * @throws IllegalArgumentException if [vertices] is empty.
     */
    public fun clip(vertices: List<CoordGrid>): Map<MapSquareKey, List<CoordGrid>> {
        require(vertices.isNotEmpty()) { "List of vertices must not be empty." }
        val bounds = resolveBoundsOrNull(vertices)
        if (bounds == null) {
            val singleKey = MapSquareKey.from(vertices.first())
            return mapOf(singleKey to vertices)
        }
        val length = MapSquareGrid.LENGTH
        val result = mutableMapOf<MapSquareKey, List<CoordGrid>>()
        for (squareX in bounds.minX..bounds.maxX) {
            for (squareZ in bounds.minZ..bounds.maxZ) {
                var subject = vertices

                subject = clip(subject, Axis.X, squareX * length, keepGE = true)
                if (subject.isEmpty()) {
                    continue
                }

                subject = clip(subject, Axis.X, (squareX + 1) * length - 1, keepGE = false)
                if (subject.isEmpty()) {
                    continue
                }

                subject = clip(subject, Axis.Z, squareZ * length, keepGE = true)
                if (subject.isEmpty()) {
                    continue
                }

                subject = clip(subject, Axis.Z, (squareZ + 1) * length - 1, keepGE = false)
                if (subject.isEmpty()) {
                    continue
                }

                val key = MapSquareKey(squareX, squareZ)
                val distinct = subject.distinct()
                result[key] = distinct
            }
        }
        return result
    }

    private fun clip(
        subject: List<CoordGrid>,
        axis: Axis,
        boundary: Int,
        keepGE: Boolean,
    ): List<CoordGrid> {
        if (subject.isEmpty()) {
            return subject
        }
        val output = mutableListOf<CoordGrid>()
        val n = subject.size
        for (i in 0 until n) {
            val curr = subject[i]
            val prev = subject[(i + n - 1) % n]

            val currVal = if (axis == Axis.X) curr.x else curr.z
            val prevVal = if (axis == Axis.X) prev.x else prev.z

            val currInside = if (keepGE) currVal >= boundary else currVal <= boundary
            val prevInside = if (keepGE) prevVal >= boundary else prevVal <= boundary

            if (currInside) {
                if (!prevInside) {
                    output.add(intersect(prev, curr, axis, boundary))
                }
                output.add(curr)
            } else if (prevInside) {
                output.add(intersect(prev, curr, axis, boundary))
            }
        }
        return output
    }

    private fun intersect(p1: CoordGrid, p2: CoordGrid, axis: Axis, boundary: Int): CoordGrid =
        if (axis == Axis.X) {
            val dx = (p2.x - p1.x).toDouble()
            val dz = (p2.z - p1.z).toDouble()
            val t = (boundary - p1.x) / dx
            val z = (p1.z + dz * t).roundToInt()
            CoordGrid(boundary, z, p1.level)
        } else {
            val dx = (p2.x - p1.x).toDouble()
            val dz = (p2.z - p1.z).toDouble()
            val t = (boundary - p1.z) / dz
            val x = (p1.x + dx * t).roundToInt()
            CoordGrid(x, boundary, p1.level)
        }

    private fun resolveBoundsOrNull(vertices: List<CoordGrid>): Bound? {
        val mapSquares = vertices.asSequence().map(MapSquareKey::from).distinct().toList()
        return if (mapSquares.size == 1) {
            null
        } else {
            resolveBounds(mapSquares)
        }
    }

    private fun resolveBounds(mapSquares: Iterable<MapSquareKey>): Bound {
        var minX = Int.MAX_VALUE
        var minZ = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var maxZ = Int.MIN_VALUE
        for (key in mapSquares) {
            minX = min(minX, key.x)
            minZ = min(minZ, key.z)
            maxX = max(maxX, key.x)
            maxZ = max(maxZ, key.z)
        }
        return Bound(minX, maxX, minZ, maxZ)
    }

    private data class Bound(val minX: Int, val maxX: Int, val minZ: Int, val maxZ: Int)

    private enum class Axis {
        X,
        Z,
    }
}
