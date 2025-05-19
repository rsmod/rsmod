package org.rsmod.api.type.builders.map.tile

import org.rsmod.api.type.builders.map.MapResourceFile
import org.rsmod.api.type.builders.map.MapTypeBuilder

public abstract class MapTileBuilder : MapTypeBuilder() {
    @PublishedApi internal val resources: MutableList<MapResourceFile> = mutableListOf()

    /**
     * Registers map terrain data to pack during the map-packing task.
     *
     * _**Important**: This is only invoked by the Gradle `packCache` task and is **not** executed
     * during normal server startup. Any changes to this builder will not affect the game unless the
     * task is manually run._
     *
     * ### Example Usage
     *
     * ```
     * override fun onPackMapTask() {
     *    // Packs a binary map terrain file from a resource path. The file name must start with 'm'
     *    // and follow the format `m[x]_[z]` (e.g., `m50_50`, with no extension), where the numbers
     *    // correspond to the map square key's x and z values.
     *    // The file content must match the structure expected by [MapTileDecoder].
     *    resourceFile<MyTileBuilder>("map/m50_50")
     * }
     * ```
     */
    abstract override fun onPackMapTask()

    public inline fun <reified T> resourceFile(path: String) {
        val file = MapResourceFile(T::class.java, path)
        resources += file
    }
}
