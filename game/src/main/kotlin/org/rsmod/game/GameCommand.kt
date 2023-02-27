package org.rsmod.game

import com.github.ajalt.clikt.core.CliktCommand
import com.github.michaelbull.logging.InlineLogger
import com.google.inject.AbstractModule
import com.google.inject.Guice

public fun main(args: Array<String>): Unit = GameCommand().main(args)

public class GameCommand : CliktCommand(name = "game") {

    override fun run() {
        val injector = Guice.createInjector(GameStandaloneModule)
        val bootstrap = injector.getInstance(GameBootstrap::class.java)
        bootstrap.startUp()
    }

    private object GameStandaloneModule : AbstractModule() {

        override fun configure() {
            install(GameModule)
            bind(GameProcess::class.java).toInstance(process)
        }

        private val process = object : GameProcess {

            private val logger = InlineLogger()

            override fun startUp() {
                logger.info { "Standalone game started up" }
            }

            override fun shutDown() {
                logger.info { "Standalone game shutting down" }
            }

            override fun cycle() {
                logger.debug { "Standalone game cycle" }
            }
        }
    }
}
