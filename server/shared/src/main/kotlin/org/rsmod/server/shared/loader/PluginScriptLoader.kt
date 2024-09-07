package org.rsmod.server.shared.loader

import com.google.inject.Injector
import io.github.classgraph.ClassGraph
import org.rsmod.plugin.scripts.PluginScript

object PluginScriptLoader {
    /** @param packages package names which will be accepted; leave empty for default. */
    fun <T : PluginScript> load(
        type: Class<T>,
        packages: Array<String>,
        injector: Injector,
        lenient: Boolean = false,
    ): Collection<T> {
        val plugins = mutableListOf<T>()
        val scanner = ClassGraph().enableClassInfo().acceptPackages(*packages)
        val scan = scanner.scan()
        scan.use { result ->
            val infoList = result.getSubclasses(type)
            infoList.forEach { info ->
                val clazz = info.loadClass(type)
                try {
                    val instance = injector.getInstance(clazz)
                    plugins += instance
                } catch (t: Throwable) {
                    if (!lenient) throw (t.cause ?: t)
                    t.printStackTrace()
                }
            }
        }
        return plugins
    }
}
