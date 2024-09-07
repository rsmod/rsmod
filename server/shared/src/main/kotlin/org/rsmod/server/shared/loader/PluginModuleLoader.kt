package org.rsmod.server.shared.loader

import io.github.classgraph.ClassGraph
import org.rsmod.plugin.module.PluginModule

object PluginModuleLoader {
    /** @param packages package names which will be accepted; leave empty for default. */
    fun <T : PluginModule> load(type: Class<T>, packages: Array<String>): Collection<T> {
        val modules = mutableListOf<T>()
        val scanner =
            ClassGraph()
                .enableClassInfo()
                .rejectPackages(PluginModule::class.java.packageName)
                .acceptPackages(*packages)
        val scan = scanner.scan()
        scan.use { result ->
            val infoList = result.getSubclasses(type).directOnly()
            infoList.forEach { info ->
                val clazz = info.loadClass(type)
                val ctor = clazz.getConstructor()
                val instance = ctor.newInstance()
                modules += instance
            }
        }
        return modules
    }
}
