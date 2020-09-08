package gg.rsmod.game

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import gg.rsmod.game.event.EventBus
import gg.rsmod.game.service.GameServiceList

class GameModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<GameServiceList>()
            .`in`(scope)
        bind<EventBus>()
            .`in`(scope)
    }
}
