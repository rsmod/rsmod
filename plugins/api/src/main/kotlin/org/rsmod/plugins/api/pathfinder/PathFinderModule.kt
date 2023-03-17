package org.rsmod.plugins.api.pathfinder

import com.google.inject.AbstractModule
import com.google.inject.Scopes

public object PathFinderModule : AbstractModule() {

    override fun configure() {
        bind(PathValidator::class.java).`in`(Scopes.SINGLETON)
        bind(RouteFactory::class.java).`in`(Scopes.SINGLETON)
        bind(StepFactory::class.java).`in`(Scopes.SINGLETON)
    }
}
