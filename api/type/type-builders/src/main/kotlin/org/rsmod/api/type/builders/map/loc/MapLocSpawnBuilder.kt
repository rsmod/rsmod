package org.rsmod.api.type.builders.map.loc

import org.rsmod.api.type.builders.map.MapTypeBuilder

public abstract class MapLocSpawnBuilder : MapTypeBuilder() {
    /**
     * Registers loc spawns to pack during the map-packing task.
     *
     * _**Important**: This is only invoked by the Gradle `packCache` task and is **not** executed
     * during normal server startup. Any changes to this builder will not affect the game unless the
     * task is manually run._
     *
     * ### Example Usage
     *
     * ```
     * override fun onPackMapTask() {
     *    // Packs a binary loc spawn file from a resource path. The file name must start with 'l'
     *    // and follow the format `l[x]_[z]` (e.g., `l50_50`, with no extension), where the numbers
     *    // correspond to the map square key's x and z values.
     *    // The file content must match the structure expected by [MapLocListDecoder].
     *    resourceFile<MyLocSpawns>("map/l50_50")
     * }
     * ```
     */
    abstract override fun onPackMapTask()

    override fun cleanup() {
        resources.clear()
    }
}
