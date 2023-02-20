package org.rsmod.game.scripts.module

import io.github.classgraph.ClassGraph

public object ModuleScriptLoader {

    public fun <T : ModuleScript> load(type: Class<T>): List<T> {
        val modules = mutableListOf<T>()
        ClassGraph().enableAllInfo().scan().use { scan ->
            val infoList = scan.getSubclasses(type).directOnly()
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
