package org.rsmod.game.scripts.module

import com.google.inject.AbstractModule

public class ModuleMap<K>(
    private val modules: MutableMap<K, MutableList<AbstractModule>> = mutableMapOf()
) : Map<K, List<AbstractModule>> by modules {

    public fun install(module: AbstractModule, key: K) {
        val mapped = modules.getOrPut(key) { mutableListOf() }
        mapped += module
    }
}
