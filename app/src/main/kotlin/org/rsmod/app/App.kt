package org.rsmod.app

import com.github.ajalt.clikt.core.CliktCommand
import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Guice
import com.google.inject.Injector
import org.rsmod.game.GameBootstrap
import org.rsmod.game.GameModule
import org.rsmod.game.plugins.PluginLoader
import org.rsmod.game.plugins.content.KotlinScriptContentPlugin

private val logger = InlineLogger()

public fun main(args: Array<String>): Unit = AppCommand().main(args)

public class AppCommand : CliktCommand(name = "app") {

    override fun run() {
        val injector = Guice.createInjector(GameModule())
        loadPlugins(injector)
        startUpGame(injector)
    }

    private fun loadPlugins(injector: Injector) {
        val loader = injector.getInstance(PluginLoader::class.java)
        val plugins = loader.load(KotlinScriptContentPlugin::class.java)
        logger.info { "Loaded ${plugins.size} plugin${if (plugins.size == 1) "" else "s"}." }
    }

    private fun startUpGame(injector: Injector) {
        val bootstrap = injector.getInstance(GameBootstrap::class.java)
        bootstrap.startUp()
    }
}
