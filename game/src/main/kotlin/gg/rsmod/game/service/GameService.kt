package gg.rsmod.game.service

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject

private val logger = InlineLogger()

interface GameService {
    fun start()
    fun shutdown()
    fun cycle()
}

class GameServiceList(
    private val services: MutableList<GameService>
) : List<GameService> by services {

    @Inject
    constructor() : this(mutableListOf())

    fun register(init: GameServiceBuilder.() -> Unit) {
        val builder = GameServiceBuilder().apply(init)
        services.addAll(builder.services)
    }
}

@DslMarker
private annotation class GameServiceDsl

@GameServiceDsl
class GameServiceBuilder(
    internal val services: MutableList<GameService> = mutableListOf()
) {

    operator fun GameService.unaryMinus() {
        logger.debug { "Append service to builder (service=$this)" }
        services.add(this)
    }
}
