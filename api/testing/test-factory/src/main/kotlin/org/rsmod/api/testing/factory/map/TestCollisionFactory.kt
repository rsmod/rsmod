package org.rsmod.api.testing.factory.map

import org.rsmod.pathfinder.collision.CollisionFlagMap

public class TestCollisionFactory {
    /**
     * Creates a new instance of [CollisionFlagMap] with the specified number of zones.
     *
     * The [zoneCount] parameter sets the number of zones to allocate in the collision map. Zones
     * are allocated along the X coordinate first, filling horizontally. To allocate zones across
     * both X and Z coordinates, you must allocate enough zones to fill the entire width of the map
     * ([org.rsmod.map.CoordGrid.MAP_WIDTH]) before additional zones are allocated along the Z
     * coordinate. In such cases, consider using [borrowSharedMap] instead.
     *
     * @param zoneCount The number of zones to allocate in the collision map. Defaults to
     *   [DEFAULT_ZONE_CAPACITY] to conserve memory.
     * @see [borrowSharedMap]
     */
    public fun create(zoneCount: Int = DEFAULT_ZONE_CAPACITY): CollisionFlagMap =
        CollisionFlagMap(flags = arrayOfNulls(zoneCount))

    /**
     * Returns a shared instance of [CollisionFlagMap] that spans the entire game map.
     *
     * This method provides a pre-allocated [CollisionFlagMap] covering the full extent of the game
     * map, intended for tests needing a complete collision map. The instance is shared across
     * tests, with its zones reset before each use to avoid cross-test contamination.
     *
     * **Note:** Using this shared instance when not necessary may slow down tests due to increased
     * memory usage and potential contention when accessing the shared resource. It is recommended
     * to use [create] instead, unless you specifically require collision data for the entire game
     * map or a large number of zones.
     *
     * @see [create]
     */
    public fun borrowSharedMap(): CollisionFlagMap = borrowFullCollisionMap()

    public companion object {
        public const val DEFAULT_ZONE_CAPACITY: Int = 1

        private val threadLocalCollision = ThreadLocal.withInitial { CollisionFlagMap() }

        /**
         * Retrieves and resets a shared full collision map from thread-local storage.
         *
         * This method provides a thread-safe way to access a full collision map for testing. The
         * map's flags are reset before returning to ensure no leftover state from previous tests.
         *
         * @return A reset [CollisionFlagMap] spanning the entire game map.
         */
        private fun borrowFullCollisionMap(): CollisionFlagMap =
            threadLocalCollision.get().apply { flags.fill(null) }
    }
}
