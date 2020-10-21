package gg.rsmod.plugins.content.pathfinder.module

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import gg.rsmod.plugins.content.pathfinder.PathFinder
import gg.rsmod.plugins.content.pathfinder.dummy.DummyPathFinder

class PathFinderModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<PathFinder>()
            .to<DummyPathFinder>()
            .`in`(scope)
    }
}
