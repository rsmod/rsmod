package org.rsmod.api.type.builders.map.npc

import org.rsmod.api.type.builders.map.MapTypeBuilder
import org.rsmod.api.type.builders.resource.TypeResourceFile

public abstract class MapNpcSpawnBuilder : MapTypeBuilder() {
    @PublishedApi internal val resources: MutableList<TypeResourceFile> = mutableListOf()

    /**
     * Registers npc spawns to pack during the map-packing task.
     *
     * _**Important**: This is only invoked by the Gradle `packCache` task and is **not** executed
     * during normal server startup. Any changes to this builder will not affect the game unless the
     * task is manually run._
     *
     * ### Example Usage
     *
     * ```
     * override fun onPackMapTask() {
     *    // Packs a binary npc spawn file from a resource path. The file name must start with 'n'
     *    // and follow the format `n[x]_[z]` (e.g., `n50_50`, with no extension), where the numbers
     *    // correspond to the map square key's x and z values.
     *    // The file content must match the structure expected by [MapNpcListDecoder].
     *    resourceFile<MyNpcSpawns>("map/n50_50")
     *
     *    // Packs a TOML npc spawn file from a resource path. The file must end in `.toml` and
     *    // define a list named `spawn`, with each entry containing an `npc` name and `coords`
     *    // string (e.g., '0_50_50_0_0'). The name is resolved using [NameMapping] symbols.
     *    resourceFile<MyNpcSpawns>("map/npcs.toml")
     * }
     * ```
     */
    abstract override fun onPackMapTask()

    override fun cleanup() {
        resources.clear()
    }

    public inline fun <reified T> resourceFile(path: String) {
        val file = TypeResourceFile(T::class.java, path)
        resources += file
    }
}
