package org.rsmod.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.michaelbull.logging.InlineLogger
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.util.Modules
import org.rsmod.game.GameBootstrap
import org.rsmod.game.GameModule
import org.rsmod.game.config.GameConfig
import org.rsmod.game.config.GameConfigModule
import org.rsmod.game.model.GameEnv
import org.rsmod.game.scripts.module.KotlinScriptModule
import org.rsmod.game.scripts.module.ModuleBranch
import org.rsmod.game.scripts.module.ModuleScriptLoader
import org.rsmod.game.scripts.plugin.KotlinScriptPlugin
import org.rsmod.game.scripts.plugin.ScriptPluginLoader

private val logger = InlineLogger()

public fun main(args: Array<String>): Unit = AppCommand().main(args)

public class AppCommand : CliktCommand(name = "app") {

    override fun run() {
        val scriptModules = loadPluginModules(injectGameConfig().env)
        val combinedModules = Modules.combine(GameModule, *scriptModules.toTypedArray())
        val injector = Guice.createInjector(combinedModules)
        loadContentPlugins(injector)
        startUpGame(injector)
    }

    private fun loadPluginModules(env: GameEnv): List<AbstractModule> {
        val moduleScripts = ModuleScriptLoader.load(KotlinScriptModule::class.java)
        val branchModules = moduleScripts.flatMap { it.branchModules[env.moduleBranch] ?: emptyList() }
        val modules = moduleScripts.flatMap { it.modules } + branchModules
        logger.info {
            "Loaded ${modules.size} module script${if (modules.size == 1) "" else "s"}. " +
                "(${branchModules.size} branch-specific)"
        }
        return modules
    }

    private fun loadContentPlugins(injector: Injector) {
        val plugins = ScriptPluginLoader.load(KotlinScriptPlugin::class.java, injector)
        logger.info { "Loaded ${plugins.size} plugin script${if (plugins.size == 1) "" else "s"}." }
    }

    private fun startUpGame(injector: Injector) {
        val bootstrap = injector.getInstance(GameBootstrap::class.java)
        val config = injector.getInstance(GameConfig::class.java)
        logger.info { "Loaded game with config: $config" }
        bootstrap.startUp()
    }

    private fun injectGameConfig(): GameConfig {
        val injector = Guice.createInjector(GameConfigModule)
        return injector.getInstance(GameConfig::class.java)
    }

    private val GameEnv.moduleBranch: ModuleBranch
        get() = when (this) {
            GameEnv.Dev -> ModuleBranch.Dev
            GameEnv.Prod -> ModuleBranch.Prod
            GameEnv.Test -> ModuleBranch.Test
        }
}
