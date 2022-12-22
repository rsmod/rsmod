package org.rsmod.game.plugins.content

import com.google.inject.Injector
import io.github.classgraph.ClassGraph
import javax.inject.Inject

public class ContentPluginLoader @Inject constructor(
    private val injector: Injector
) {

    public fun <T> load(type: Class<T>, lenient: Boolean = false): List<T> {
        val plugins = mutableListOf<T>()
        ClassGraph().enableAllInfo().scan().use { scan ->
            val infoList = scan.getSubclasses(type).directOnly()
            infoList.forEach { info ->
                val clazz = info.loadClass(type)
                val ctor = clazz.getConstructor(Injector::class.java)
                try {
                    val instance = ctor.newInstance(injector)
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
