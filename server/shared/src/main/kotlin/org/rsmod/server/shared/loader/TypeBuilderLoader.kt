package org.rsmod.server.shared.loader

import io.github.classgraph.ClassGraph
import jakarta.inject.Inject
import kotlin.reflect.KVisibility
import org.rsmod.annotations.PluginGraph
import org.rsmod.api.type.builders.TypeBuilder

class TypeBuilderLoader @Inject constructor(@PluginGraph private val scanner: ClassGraph) {
    fun load(): Collection<TypeBuilder<*, *>> {
        val builders = mutableListOf<TypeBuilder<*, *>>()
        scanner.scan().use { result ->
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
