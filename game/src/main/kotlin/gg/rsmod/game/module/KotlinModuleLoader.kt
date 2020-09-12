package gg.rsmod.game.module

import com.google.inject.Scope
import io.github.classgraph.ClassGraph

class KotlinModuleLoader(
    private val scope: Scope
) {

    fun load(): List<KotlinGameModule> {
        val modules = mutableListOf<KotlinGameModule>()
        ClassGraph().enableAllInfo().scan().use { result ->
            val subclasses = result.getSubclasses(KotlinGameModule::class.java.name).directOnly()
            subclasses.forEach { subclass ->
                val loadedClass = subclass.loadClass(KotlinGameModule::class.java)
                val constructor = loadedClass.getConstructor(Scope::class.java)
                val instance = constructor.newInstance(scope)
                modules.add(instance)
            }
        }
        return modules
    }
}
