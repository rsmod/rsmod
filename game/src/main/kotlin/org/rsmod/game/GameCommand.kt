package org.rsmod.game

import com.github.ajalt.clikt.core.CliktCommand
import com.google.inject.Guice

public fun main(args: Array<String>): Unit = GameCommand().main(args)

public class GameCommand : CliktCommand(name = "game") {

    override fun run() {
        val injector = Guice.createInjector(GameModule)
        val bootstrap = injector.getInstance(GameBootstrap::class.java)
        bootstrap.startUp()
    }
}
