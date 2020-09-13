package gg.rsmod.game.dispatch

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule

class DispatcherModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<GameDispatcher>()
            .`in`(scope)
    }
}
