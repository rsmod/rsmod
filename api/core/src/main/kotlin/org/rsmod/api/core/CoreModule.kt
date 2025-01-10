package org.rsmod.api.core

import com.google.inject.Provider
import jakarta.inject.Inject
import org.rsmod.api.cache.CacheModule
import org.rsmod.api.core.module.EntityRepoModule
import org.rsmod.api.core.module.GameMapModule
import org.rsmod.api.core.module.PlayerModule
import org.rsmod.api.core.module.TypeModule
import org.rsmod.api.game.process.GameCycle
import org.rsmod.api.market.MarketModule
import org.rsmod.api.random.RandomModule
import org.rsmod.api.route.RouteModule
import org.rsmod.game.queue.WorldQueueList
import org.rsmod.game.type.enums.EnumTypeList
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.module.ExtendedModule

public object CoreModule : ExtendedModule() {
    override fun bind() {
        install(CacheModule)
        install(EntityRepoModule)
        install(GameMapModule)
        install(MarketModule)
        install(PlayerModule)
        install(RandomModule)
        install(RouteModule)
        install(TypeModule)
        bindInstance<GameCycle>()
        bindInstance<WorldQueueList>()
        bindProvider(EnumTypeMapResolverProvider::class.java)
    }

    private class EnumTypeMapResolverProvider @Inject constructor(private val enums: EnumTypeList) :
        Provider<EnumTypeMapResolver> {
        override fun get(): EnumTypeMapResolver = EnumTypeMapResolver(enums)
    }
}
