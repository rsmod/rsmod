package org.rsmod.api.type.builders.clientscript

import org.rsmod.api.type.builders.resource.ResourceTypeBuilder

public abstract class ClientScriptBuilder : ResourceTypeBuilder() {
    /**
     * Registers clientscript data to pack during the cs2-packing task.
     *
     * _**Important**: This is only invoked by the Gradle `packCache` task and is **not** executed
     * during normal server startup. Any changes to this builder will not affect the game unless the
     * task is manually run._
     *
     * ### Example Usage
     *
     * ```
     * override fun onPackCs2Task() {
     *    // Packs a binary cs2 file from a resource path. The file name must be the clientscript
     *    // symbol name and must not have a file extension. The file content must match the
     *    // structure expected by [ClientScriptByteEncoder].
     *    resourceFile<MyCs2Builder>("cs2/[clientscript,tt_rewards_init]")
     * }
     * ```
     */
    public abstract fun onPackCs2Task()

    override fun cleanup() {
        resources.clear()
    }
}
