package org.rsmod.game.plugin.kotlin

import com.google.inject.Inject
import com.google.inject.Injector
import org.rsmod.game.action.ActionBus
import org.rsmod.game.event.EventBus
import org.rsmod.game.cmd.CommandMap
import io.github.classgraph.ClassGraph

class KotlinPluginLoader @Inject constructor(
    private val injector: Injector,
    private val eventBus: EventBus,
    private val actions: ActionBus,
    private val commands: CommandMap
) {

    fun load(): List<KotlinPlugin> {
        val plugins = mutableListOf<KotlinPlugin>()
        ClassGraph().enableAllInfo().scan().use { result ->
            val subclasses = result.getSubclasses(KotlinPlugin::class.java.name).directOnly()
            subclasses.forEach { subclass ->
                val loadedClass = subclass.loadClass(KotlinPlugin::class.java)
                val constructor = loadedClass.getConstructor(
                    Injector::class.java,
                    EventBus::class.java,
                    ActionBus::class.java,
                    CommandMap::class.java
                )
                try {
                    val instance = constructor.newInstance(
                        injector,
                        eventBus,
                        actions,
                        commands
                    )
                    plugins.add(instance)
                } catch (t: Throwable) {
                    throw t.cause ?: t
                }
            }
        }
        return plugins
    }
}
