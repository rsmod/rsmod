package org.rsmod.server.shared.loader

import com.google.inject.Injector
import io.github.classgraph.ClassGraph
import jakarta.inject.Inject
import java.util.concurrent.Executors
import org.rsmod.annotations.PluginGraph
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.server.shared.util.use

class PluginScriptLoader @Inject constructor(@PluginGraph private val scanner: ClassGraph) {
    fun <T : PluginScript> load(
        type: Class<T>,
        injector: Injector,
        lenient: Boolean = false,
    ): Collection<T> {
        val plugins = mutableListOf<T>()
        val parallelism = Runtime.getRuntime().availableProcessors()
        val threadPool = Executors.newFixedThreadPool(parallelism)
        val scan = threadPool.use { scanner.scan(it, parallelism) }
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
