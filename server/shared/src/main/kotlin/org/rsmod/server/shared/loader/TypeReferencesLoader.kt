package org.rsmod.server.shared.loader

import io.github.classgraph.ClassGraph
import jakarta.inject.Inject
import java.util.concurrent.Executors
import kotlin.reflect.KVisibility
import org.rsmod.annotations.PluginGraph
import org.rsmod.api.type.refs.TypeReferences

class TypeReferencesLoader @Inject constructor(@PluginGraph private val scanner: ClassGraph) {
    fun load(): Collection<TypeReferences<*, *>> {
        val references = mutableListOf<TypeReferences<*, *>>()
        val parallelism = Runtime.getRuntime().availableProcessors()
        val scan = scanner.scan(Executors.newFixedThreadPool(parallelism), parallelism)
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
