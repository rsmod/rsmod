package org.rsmod.game.plugins.module

import com.google.inject.AbstractModule

public open class ModulePlugin(public val modules: ModuleSet = ModuleSet()) {

    public fun install(module: AbstractModule) {
        modules += module
    }
}
