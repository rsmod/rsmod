package org.rsmod.game.scripts.plugin

import com.google.inject.Injector
import io.github.classgraph.ClassGraph

public object ScriptPluginLoader {

    public fun <T : ScriptPlugin> load(type: Class<T>, injector: Injector, lenient: Boolean = false): List<T> {
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
