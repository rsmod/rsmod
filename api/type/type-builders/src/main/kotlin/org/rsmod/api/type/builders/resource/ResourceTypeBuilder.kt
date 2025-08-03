package org.rsmod.api.type.builders.resource

public abstract class ResourceTypeBuilder {
    @PublishedApi internal val resources: MutableList<TypeResourceFile> = mutableListOf()

    public inline fun <reified T> resourceFile(scriptName: String) {
        val file = TypeResourceFile(T::class.java, scriptName)
        resources += file
    }

    /**
     * Clears [resources] and any additional implementation-specific state.
     *
     * This ensures that builder pack tasks are idempotent and can safely be run multiple times
     * (e.g., during server restarts or repacking).
     *
     * _Implementations must always clear [resources] as well as any custom collections (e.g., an
     * area builder should also clear its "polygons" list)._
     */
    public abstract fun cleanup()
}
