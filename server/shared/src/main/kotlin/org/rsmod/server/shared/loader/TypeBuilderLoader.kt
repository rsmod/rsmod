package org.rsmod.server.shared.loader

import io.github.classgraph.ClassGraph
import jakarta.inject.Inject
import java.util.concurrent.Executors
import kotlin.reflect.KVisibility
import org.rsmod.annotations.PluginGraph
import org.rsmod.api.type.builders.TypeBuilder
import org.rsmod.server.shared.util.use

class TypeBuilderLoader @Inject constructor(@PluginGraph private val scanner: ClassGraph) {
    fun load(): Collection<TypeBuilder<*, *>> {
        val builders = mutableListOf<TypeBuilder<*, *>>()
        val parallelism = Runtime.getRuntime().availableProcessors()
        val threadPool = Executors.newFixedThreadPool(parallelism)
        val scan = threadPool.use { scanner.scan(it, parallelism) }
        scan.use { result ->
            val subclasses = result.getSubclasses(TypeBuilder::class.java)
            for (info in subclasses) {
                val clazz = info.loadClass(TypeBuilder::class.java)
                if (clazz.kotlin.visibility == KVisibility.PRIVATE) {
                    error("TypeBuilder subclasses must not be marked as private: ${clazz.name}")
                }
                val instance = clazz.kotlin.objectInstance ?: continue
                builders += instance
            }
        }
        return builders
    }
}
