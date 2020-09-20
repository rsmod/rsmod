package gg.rsmod.game

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import gg.rsmod.game.action.ActionHandlerMap
import gg.rsmod.game.action.ActionMap
import gg.rsmod.game.event.EventBus
import gg.rsmod.game.model.mob.NpcList
import gg.rsmod.game.model.mob.PlayerList

class GameModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<Game>()
            .`in`(scope)

        bind<EventBus>()
            .`in`(scope)

        bind<ActionHandlerMap>()
            .`in`(scope)

        bind<ActionMap>()
            .`in`(scope)

        bind<PlayerList>()
            .`in`(scope)

        bind<NpcList>()
            .`in`(scope)
    }
}
