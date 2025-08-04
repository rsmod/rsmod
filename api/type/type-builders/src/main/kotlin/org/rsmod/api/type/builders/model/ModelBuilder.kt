package org.rsmod.api.type.builders.model

import org.rsmod.api.type.builders.resource.ResourceTypeBuilder

public abstract class ModelBuilder : ResourceTypeBuilder() {
    /**
     * Registers model data to pack during the model-packing task.
     *
     * _**Important**: This is only invoked by the Gradle `packCache` task and is **not** executed
     * during normal server startup. Any changes to this builder will not affect the game unless the
     * task is manually run._
     *
     * ### Example Usage
     *
     * ```
     * override fun onPackModelTask() {
     *    // Packs a binary model file from a resource path. The file name must be the model
     *    // symbol name and must not have a file extension. The file content must match the
     *    // structure expected by [ModelByteEncoder].
     *    resourceFile<MyModelBuilder>("models/example_model")
     * }
     * ```
     */
    public abstract fun onPackModelTask()

    override fun cleanup() {
        resources.clear()
    }
}
