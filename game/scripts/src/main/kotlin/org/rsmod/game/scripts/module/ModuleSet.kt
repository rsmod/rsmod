package org.rsmod.game.scripts.module

import com.google.inject.AbstractModule

public class ModuleSet(
    private val modules: MutableSet<AbstractModule> = mutableSetOf()
) : Set<AbstractModule> by modules {

    public operator fun plusAssign(v: AbstractModule) {
        modules += v
    }
}
