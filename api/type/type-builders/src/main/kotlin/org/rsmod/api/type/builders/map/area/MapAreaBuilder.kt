package org.rsmod.api.type.builders.map.area

import org.rsmod.api.type.builders.map.MapTypeBuilder
import org.rsmod.game.area.polygon.PolygonArea
import org.rsmod.game.area.polygon.PolygonAreaBuilder
import org.rsmod.game.type.area.AreaType
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
     *      polygon {
     *          // Max values (e.g., `4`) are inclusive.
     *          vertex(CoordGrid(0, 50, 50, 0, 0))
     *          vertex(CoordGrid(0, 50, 50, 0, 4))
     *          vertex(CoordGrid(0, 50, 50, 4, 4))
     *          vertex(CoordGrid(0, 50, 50, 4, 0))
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
         * _Do not mix disconnected shapes in the same `polygon {}` block._ All vertices within the
         * block are implicitly connected in the order they are defined.
         *
         * Example:
         * ```
         * area(areas.multiway) {
         *     // First polygon
         *     polygon {
         *         vertex(CoordGrid(0, 45, 51, 53, 8))
         *         vertex(CoordGrid(0, 45, 51, 47, 8))
         *         vertex(CoordGrid(0, 45, 51, 47, 4))
         *         vertex(CoordGrid(0, 45, 51, 53, 4))
         *     }
         *
         *     // Second, disconnected polygon
         *     polygon {
         *         vertex(CoordGrid(0, 45, 51, 58, 16))
         *         vertex(CoordGrid(0, 45, 51, 58, 12))
         *         vertex(CoordGrid(0, 45, 51, 55, 12))
         *         vertex(CoordGrid(0, 45, 51, 55, 16))
         *     }
         * }
         * ```
         */
        public fun polygon(init: (PolygonAreaBuilder).() -> Unit) {
            val builder = PolygonAreaBuilder(area)
            builder.apply(init)
            cache += builder.build()
        }

        /**
         * Applies this area to the entire 64x64x4 tile region of the given [key].
         *
         * This is a shorthand for marking an entire map square (across all height levels) as
         * belonging to the specified area, without needing to define polygon vertices manually.
         */
        public fun mapSquare(key: MapSquareKey) {
            val builder = PolygonAreaBuilder(area)
            builder.mapSquare(key)
            cache += builder.build()
        }
    }
}
