package org.runerealm.server.shared.loader

import io.github.classgraph.ClassGraph
import jakarta.inject.Inject
import java.util.concurrent.Executors
import kotlin.reflect.KVisibility
import org.runerealm.annotations.PluginGraph
import org.runerealm.api.type.refs.TypeReferences
import org.runerealm.server.shared.util.use

class TypeReferencesLoader @Inject constructor(@PluginGraph private val scanner: ClassGraph) {
    fun load(): Collection<TypeReferences<*, *>> {
        val references = mutableListOf<TypeReferences<*, *>>()
        val parallelism = Runtime.getRuntime().availableProcessors()
        val threadPool = Executors.newFixedThreadPool(parallelism)
        val scan = threadPool.use { scanner.scan(it, parallelism) }
        scan.use { result ->
            val subclasses = result.getSubclasses(TypeReferences::class.java)
            for (info in subclasses) {
                val clazz = info.loadClass(TypeReferences::class.java)
                if (clazz.kotlin.visibility == KVisibility.PRIVATE) {
                    error("TypeReferences subclasses must not be marked as private: ${clazz.name}")
                }
                val instance = clazz.kotlin.objectInstance ?: continue
                references += instance
            }
        }
        return references
    }
}
