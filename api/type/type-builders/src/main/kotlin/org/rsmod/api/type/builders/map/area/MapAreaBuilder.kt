package org.rsmod.api.type.builders.map.area

import org.rsmod.api.type.builders.map.MapTypeBuilder
import org.rsmod.game.area.polygon.PolygonArea
import org.rsmod.game.area.polygon.PolygonAreaBuilder
import org.rsmod.game.area.polygon.VertexCoord
import org.rsmod.game.type.area.AreaType
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareKey

@DslMarker private annotation class BuilderDslMarker

@BuilderDslMarker
public abstract class MapAreaBuilder : MapTypeBuilder<PolygonArea>() {
    /**
     * Registers area polygons to pack during the map-packing task.
     *
     * _**Important**: This is only invoked by the Gradle `packCache` task and is **not** executed
     * during normal server startup. Any changes to this builder will not affect the game unless the
     * task is manually run._
     *
     * ### Example Usage
     *
     * ```
     * override fun onPackMapTask() {
     *    // Packs `multiway` area into the entire 64x64x4 tiles in map square (50, 50).
     *    area(areas.multiway) {
     *      mapSquare(MapSquareKey(50, 50))
     *    }
     *
     *    // Packs `singles_plus` area into a 5x5 square defined by the vertices below.
     *    // Note: `polygon` can handle any complex or concave shapes, including ones
     *    // that span multiple map squares
     *    area(areas.singles_plus) {
     *      // Defines a polygon at the default height level of 0.
     *      polygon {
     *          // Max values (e.g., `4`) are inclusive.
     *          vertex(VertexCoord(50, 50, 0, 0))
     *          vertex(VertexCoord(50, 50, 0, 4))
     *          vertex(VertexCoord(50, 50, 4, 4))
     *          vertex(VertexCoord(50, 50, 4, 0))
     *      }
     *    }
     * }
     * ```
     *
     * _For implementation details, see [PolygonAreaBuilder] and its backing
     * [org.rsmod.game.area.polygon.PolygonMapSquareBuilder]._
     */
    abstract override fun onPackMapTask()

    public fun area(area: AreaType, init: AreaBuilder.() -> Unit) {
        AreaBuilder(area.id.toShort(), cache).apply(init)
    }

    @BuilderDslMarker
    public class AreaBuilder
    internal constructor(private val area: Short, private val cache: MutableList<PolygonArea>) {
        private var setMapSquare = false
        private var setPolygon = false

        /**
         * Defines a single polygon shape for this area.
         *
         * Each call to this function represents one **connected** polygon. All vertices defined
         * inside the [init] block will be treated as part of the same shape, with edges
         * automatically drawn between consecutive points - including an implicit edge between the
         * last and first vertices to close the shape.
         *
         * If you want to define multiple disconnected shapes (e.g., two separate rectangular
         * areas), call this function multiple times with a separate set of vertices for each.
         *
         * _Do not mix disconnected shapes in the same `polygon {}` block._ All [VertexCoord]s
         * within the block are implicitly connected in the order they are defined.
         *
         * Example:
         * ```
         * area(areas.multiway) {
         *     // First polygon
         *     polygon(level = 1) {
         *         vertex(VertexCoord(45, 51, 53, 8))
         *         vertex(VertexCoord(45, 51, 47, 8))
         *         vertex(VertexCoord(45, 51, 47, 4))
         *         vertex(VertexCoord(45, 51, 53, 4))
         *     }
         *
         *     // Second, disconnected polygon
         *     polygon(level = 1) {
         *         vertex(VertexCoord(45, 51, 58, 16))
         *         vertex(VertexCoord(45, 51, 58, 12))
         *         vertex(VertexCoord(45, 51, 55, 12))
         *         vertex(VertexCoord(45, 51, 55, 16))
         *     }
         * }
         * ```
         *
         * This method accepts a single [level] to apply the polygon to. Use the `polygon(levels =
         * listOf(...)) {}` overload to apply it across multiple levels.
         *
         * @throws IllegalArgumentException if [level] is not a valid height level `[0-3]`.
         * @throws IllegalStateException if [mapSquare] has already been set.
         * @see [VertexCoord]
         */
        public fun polygon(level: Int = 0, init: (PolygonAreaBuilder).() -> Unit) {
            requireLevel(level)
            checkMapSquare()

            val builder = PolygonAreaBuilder.withLevels(area, level..level)
            builder.apply(init)
            setPolygon = true

            cache += builder.build()
        }

        /**
         * Defines a single polygon shape for this area across multiple levels.
         *
         * Each call to this function represents one **connected** polygon. All vertices defined
         * inside the [init] block will be treated as part of the same shape, with edges
         * automatically drawn between consecutive points - including an implicit edge between the
         * last and first vertices to close the shape.
         *
         * If you want to define multiple disconnected shapes (e.g., two separate rectangular
         * areas), call this function multiple times with a separate set of vertices for each.
         *
         * _Do not mix disconnected shapes in the same `polygon {}` block._ All [VertexCoord]s
         * within the block are implicitly connected in the order they are defined.
         *
         * Example:
         * ```
         * area(areas.singles_plus) {
         *     // First polygon
         *     polygon(levels = 1..2) {
         *         vertex(VertexCoord(45, 51, 53, 8))
         *         vertex(VertexCoord(45, 51, 47, 8))
         *         vertex(VertexCoord(45, 51, 47, 4))
         *         vertex(VertexCoord(45, 51, 53, 4))
         *     }
         *
         *     // Second, disconnected polygon
         *     polygon(levels = 1..2) {
         *         vertex(VertexCoord(45, 51, 58, 16))
         *         vertex(VertexCoord(45, 51, 58, 12))
         *         vertex(VertexCoord(45, 51, 55, 12))
         *         vertex(VertexCoord(45, 51, 55, 16))
         *     }
         * }
         * ```
         *
         * This method accepts an [Iterable] of height levels, and will apply the given vertices to
         * each level in the list. If you only need to set a single level, use the `polygon(level =
         * 1)` overload instead.
         *
         * @throws IllegalArgumentException if [levels] is empty or contains any value outside
         *   `[0-3]`.
         * @throws IllegalStateException if [mapSquare] has already been set.
         * @see [VertexCoord]
         */
        public fun polygon(levels: Iterable<Int>, init: (PolygonAreaBuilder).() -> Unit) {
            requireLevels(levels)
            checkMapSquare()

            val builder = PolygonAreaBuilder.withLevels(area, levels)
            builder.apply(init)
            setPolygon = true

            cache += builder.build()
        }

        /**
         * Applies this area to the entire 64x64x4 tile region of the given [key].
         *
         * This is a shorthand for marking an entire map square (across all height levels) as
         * belonging to the specified area, without needing to define polygon vertices manually.
         *
         * @throws IllegalStateException if `polygon` has already been set, or if `mapSquare` has
         *   already been called.
         * @see [MapSquareKey]
         */
        public fun mapSquare(key: MapSquareKey) {
            checkMapSquare()
            checkPolygon()

            val builder = PolygonAreaBuilder.withAllLevels(area)
            builder.apply {
                val base = VertexCoord.from(key.toCoords(level = 0))
                vertex(base.translate(0, 0))
                vertex(base.translate(63, 0))
                vertex(base.translate(63, 63))
                vertex(base.translate(0, 63))
            }
            setMapSquare = true

            cache += builder.build()
        }

        private fun requireLevel(level: Int) {
            require(level in 0 until CoordGrid.LEVEL_COUNT) { "Invalid level: $level" }
        }

        private fun requireLevels(levels: Iterable<Int>) {
            require(levels.any()) { "`levels` must contain at least one level." }
            levels.forEach(::requireLevel)
        }

        private fun checkMapSquare() {
            check(!setMapSquare) { "Area can no longer be modified - `mapSquare` has been set." }
        }

        private fun checkPolygon() {
            check(!setPolygon) { "Cannot call `mapSquare` if a `polygon` has already been set." }
        }
    }
}
