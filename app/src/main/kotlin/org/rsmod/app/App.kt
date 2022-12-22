package org.rsmod.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.google.inject.Injector
import org.rsmod.game.GameBootstrap
import org.rsmod.game.GameModule
import org.rsmod.game.plugins.content.ContentPluginLoader
import org.rsmod.game.plugins.content.KotlinScriptContentPlugin
import org.rsmod.game.plugins.module.KotlinScriptModulePlugin
import org.rsmod.game.plugins.module.ModulePluginLoader

private val logger = InlineLogger()

public fun main(args: Array<String>): Unit = AppCommand().main(args)

public class AppCommand : CliktCommand(name = "app") {

    override fun run() {
        val modulePlugins = ModulePluginLoader.load(KotlinScriptModulePlugin::class.java)
        val modules = modulePlugins.flatMap { it.modules }
        logger.info { "Loaded ${modulePlugins.size} module plugin${if (modulePlugins.size == 1) "" else "s"}." }
        val injector = Guice.createInjector(GameModule(), *modules.toTypedArray())
        loadContentPlugins(injector)
        startUpGame(injector)
    }

    private fun loadContentPlugins(injector: Injector) {
        val loader = injector.getInstance(ContentPluginLoader::class.java)
        val plugins = loader.load(KotlinScriptContentPlugin::class.java)
        logger.info { "Loaded ${plugins.size} content plugin${if (plugins.size == 1) "" else "s"}." }
    }

    private fun startUpGame(injector: Injector) {
        val bootstrap = injector.getInstance(GameBootstrap::class.java)
        bootstrap.startUp()
    }
}
