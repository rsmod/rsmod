package org.rsmod.game.scripts.module

import com.google.inject.AbstractModule

public open class ModuleScript(
    public val modules: ModuleSet = ModuleSet(),
    public val branchModules: ModuleMap<ModuleBranch> = ModuleMap()
) {

    /**
     * Adds [module] to a collection of [AbstractModule]s that should be
     * installed for dependency injection.
     */
    public fun install(module: AbstractModule) {
        modules += module
    }

    /**
     * Adds [module] to a specialized collection of _prod-only_
     * [AbstractModule]s that should be installed for dependency
     * injection _only_ when application is running in a production
     * environment.
     */
    public fun install(module: ProdModule) {
        branchModules.install(module, ModuleBranch.Prod)
    }

    /**
     * Adds [module] to a specialized collection of _dev-only_
     * [AbstractModule]s that should be installed for dependency
     * injection _only_ when application is running in a development
     * environment.
     */
    public fun install(module: DevModule) {
        branchModules.install(module, ModuleBranch.Dev)
    }

    /**
     * Adds [module] to a specialized collection of _test-only_
     * [AbstractModule]s that should be installed for dependency
     * injection _only_ when application is running in a testing
     * environment (_alpha_, _beta_, _etc_).
     */
    public fun install(module: TestModule) {
        branchModules.install(module, ModuleBranch.Test)
    }
}
