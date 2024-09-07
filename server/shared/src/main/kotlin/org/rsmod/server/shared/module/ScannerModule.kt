package org.rsmod.server.shared.module

import com.google.inject.Provider
import io.github.classgraph.ClassGraph
import org.rsmod.annotations.PluginGraph
import org.rsmod.module.ExtendedModule
import org.rsmod.server.shared.PluginConstants

object ScannerModule : ExtendedModule() {
    private val pluginPackages: Array<String>
        get() = PluginConstants.searchPackages

    override fun bind() {
        bind(ClassGraph::class.java)
            .annotatedWith(PluginGraph::class.java)
            .toProvider(ClassGraphProvider(pluginPackages))
    }
}

private class ClassGraphProvider(private val acceptedPackages: Array<String>) :
    Provider<ClassGraph> {
    override fun get(): ClassGraph =
        ClassGraph().ignoreClassVisibility().enableClassInfo().acceptPackages(*acceptedPackages)
}
