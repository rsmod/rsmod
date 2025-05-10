package org.rsmod.server.shared.loader

import io.github.classgraph.ClassGraph
import jakarta.inject.Inject
import java.util.concurrent.Executors
import kotlin.reflect.KVisibility
import org.rsmod.annotations.PluginGraph
import org.rsmod.api.type.builders.map.MapTypeBuilder
import org.rsmod.server.shared.util.use

class MapTypeBuilderLoader @Inject constructor(@PluginGraph private val scanner: ClassGraph) {
    fun load(): Collection<MapTypeBuilder> {
        val builders = mutableListOf<MapTypeBuilder>()
        val parallelism = Runtime.getRuntime().availableProcessors()
        val threadPool = Executors.newFixedThreadPool(parallelism)
        val scan = threadPool.use { scanner.scan(it, parallelism) }
        scan.use { result ->
            val subclasses = result.getSubclasses(MapTypeBuilder::class.java)
            for (info in subclasses) {
                val clazz = info.loadClass(MapTypeBuilder::class.java)
                if (clazz.kotlin.visibility == KVisibility.PRIVATE) {
                    error("MapTypeBuilder subclasses must not be marked as private: ${clazz.name}")
                }
                val instance = clazz.kotlin.objectInstance ?: continue
                builders += instance
            }
        }
        return builders
    }
}
