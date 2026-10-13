package org.runerealm.server.shared.loader

import io.github.classgraph.ClassGraph
import jakarta.inject.Inject
import java.util.concurrent.Executors
import kotlin.reflect.KVisibility
import org.runerealm.annotations.PluginGraph
import org.runerealm.api.type.builders.resource.ResourceTypeBuilder
import org.runerealm.server.shared.util.use

class ResourceTypeBuilderLoader @Inject constructor(@PluginGraph private val scanner: ClassGraph) {
    fun load(): Collection<ResourceTypeBuilder> {
        val builders = mutableListOf<ResourceTypeBuilder>()
        val parallelism = Runtime.getRuntime().availableProcessors()
        val threadPool = Executors.newFixedThreadPool(parallelism)
        val scan = threadPool.use { scanner.scan(it, parallelism) }
        scan.use { result ->
            val subclasses = result.getSubclasses(ResourceTypeBuilder::class.java)
            for (info in subclasses) {
                val clazz = info.loadClass(ResourceTypeBuilder::class.java)
                if (clazz.kotlin.visibility == KVisibility.PRIVATE) {
                    throw IllegalStateException(
                        "ResourceTypeBuilder subclasses must not be " +
                            "marked as private: ${clazz.name}"
                    )
                }
                val instance = clazz.kotlin.objectInstance ?: continue
                builders += instance
            }
        }
        return builders
    }
}
