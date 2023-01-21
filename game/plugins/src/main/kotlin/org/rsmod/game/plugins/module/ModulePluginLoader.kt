package org.rsmod.game.plugins.module

import io.github.classgraph.ClassGraph

public object ModulePluginLoader {

    public fun <T> load(type: Class<T>): List<T> {
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
