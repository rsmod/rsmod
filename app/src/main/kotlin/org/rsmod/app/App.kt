package org.rsmod.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.michaelbull.logging.InlineLogger
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.util.Modules
import org.rsmod.game.GameBootstrap
import org.rsmod.game.GameModule
import org.rsmod.game.event.GameBootUp
import org.rsmod.game.events.EventBus
import org.rsmod.game.plugins.content.ContentPluginLoader
import org.rsmod.game.plugins.content.KotlinScriptContentPlugin
import org.rsmod.game.plugins.module.KotlinScriptModulePlugin
import org.rsmod.game.plugins.module.ModulePluginLoader

private val logger = InlineLogger()

public fun main(args: Array<String>): Unit = AppCommand().main(args)

public class AppCommand : CliktCommand(name = "app") {

    override fun run() {
        val pluginModules = loadPluginModules()
        val combined = Modules.combine(GameModule, *pluginModules.toTypedArray())
        val injector = Guice.createInjector(combined)
        val events = injector.getInstance(EventBus::class.java)
        loadContentPlugins(injector)
        publishEvents(events)
        startUpGame(injector)
    }

    private fun loadPluginModules(): List<AbstractModule> {
        val modulePlugins = ModulePluginLoader.load(KotlinScriptModulePlugin::class.java)
        val modules = modulePlugins.flatMap { it.modules }
        logger.info { "Loaded ${modules.size} module plugin${if (modules.size == 1) "" else "s"}." }
        return modules
    }

    private fun loadContentPlugins(injector: Injector) {
        val plugins = ContentPluginLoader.load(KotlinScriptContentPlugin::class.java, injector)
        logger.info { "Loaded ${plugins.size} content plugin${if (plugins.size == 1) "" else "s"}." }
    }

    private fun publishEvents(events: EventBus) {
        events += GameBootUp
    }

    private fun startUpGame(injector: Injector) {
        val bootstrap = injector.getInstance(GameBootstrap::class.java)
        bootstrap.startUp()
    }
}
