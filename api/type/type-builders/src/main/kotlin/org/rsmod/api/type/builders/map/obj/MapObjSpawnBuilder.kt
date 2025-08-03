package org.rsmod.api.type.builders.map.obj

import org.rsmod.api.type.builders.map.MapTypeBuilder

public abstract class MapObjSpawnBuilder : MapTypeBuilder() {
    /**
     * Registers obj spawns to pack during the map-packing task.
     *
     * _**Important**: This is only invoked by the Gradle `packCache` task and is **not** executed
     * during normal server startup. Any changes to this builder will not affect the game unless the
     * task is manually run._
     *
     * ### Example Usage
     *
     * ```
     * override fun onPackMapTask() {
     *    // Packs a binary obj spawn file from a resource path. The file name must start with 'o'
     *    // and follow the format `o[x]_[z]` (e.g., `o50_50`, with no extension), where the numbers
     *    // correspond to the map square key's x and z values.
     *    // The file content must match the structure expected by [MapObjListDecoder].
     *    resourceFile<MyObjSpawns>("map/o50_50")
     *
     *    // Packs a TOML obj spawn file from a resource path. The file must end in `.toml` and
     *    // define a list named `spawn`, with each entry containing an `obj` name and `coords`
     *    // string (e.g., '0_50_50_0_0'). The name is resolved using [NameMapping] symbols.
     *    resourceFile<MyObjSpawns>("map/objs.toml")
     * }
     * ```
     */
    abstract override fun onPackMapTask()

    override fun cleanup() {
        resources.clear()
    }
}
