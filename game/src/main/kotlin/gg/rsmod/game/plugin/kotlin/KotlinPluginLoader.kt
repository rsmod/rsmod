package gg.rsmod.game.plugin.kotlin

import com.google.inject.Injector
import io.github.classgraph.ClassGraph

class KotlinPluginLoader(
    private val injector: Injector
) {

    fun load(): List<KotlinPlugin> {
        val plugins = mutableListOf<KotlinPlugin>()
        ClassGraph().enableAllInfo().scan().use { result ->
            val subclasses = result.getSubclasses(KotlinPlugin::class.java.name).directOnly()
            subclasses.forEach { subclass ->
                val loadedClass = subclass.loadClass(KotlinPlugin::class.java)
                val constructor = loadedClass.getConstructor(Injector::class.java)
                val instance = constructor.newInstance(injector)
                plugins.add(instance)
            }
        }
        return plugins
    }
}
