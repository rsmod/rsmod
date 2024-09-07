package org.rsmod.api.core

import org.rsmod.api.cache.CacheModule
import org.rsmod.api.game.process.GameCycle
import org.rsmod.api.random.RandomModule
import org.rsmod.api.route.RouteModule
import org.rsmod.module.ExtendedModule

public object CoreModule : ExtendedModule() {
    override fun bind() {
        install(CacheModule)
        install(EntityRepoModule)
        install(GameMapModule)
        install(RandomModule)
        install(RouteModule)
        install(TypeModule)
        bindInstance<GameCycle>()
    }
}
