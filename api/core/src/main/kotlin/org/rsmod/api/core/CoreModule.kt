package org.rsmod.api.core

import com.google.inject.Provider
import jakarta.inject.Inject
import org.rsmod.api.account.AccountModule
import org.rsmod.api.area.checker.AreaCheckerModule
import org.rsmod.api.cache.CacheModule
import org.rsmod.api.core.module.EntityHitModule
import org.rsmod.api.core.module.GameMapModule
import org.rsmod.api.core.module.PlayerModule
import org.rsmod.api.core.module.RealmModule
import org.rsmod.api.core.module.RegistryModule
import org.rsmod.api.core.module.StatModModule
import org.rsmod.api.core.module.TypeModule
import org.rsmod.api.db.DatabaseModule
import org.rsmod.api.game.process.GameCycle
import org.rsmod.api.hunt.HuntModule
import org.rsmod.api.market.MarketModule
import org.rsmod.api.pw.hash.PasswordHashModule
import org.rsmod.api.random.RandomModule
import org.rsmod.api.route.RouteModule
import org.rsmod.api.server.config.ServerConfigModule
import org.rsmod.api.totp.TotpModule
import org.rsmod.api.utils.logging.ExceptionHandlerModule
import org.rsmod.game.dbtable.DbRowResolver
import org.rsmod.game.enums.EnumTypeMapResolver
import org.rsmod.game.queue.WorldQueueList
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.enums.EnumTypeList
import org.rsmod.module.ExtendedModule

public object CoreModule : ExtendedModule() {
    override fun bind() {
        install(AccountModule)
        install(AreaCheckerModule)
        install(CacheModule)
        install(DatabaseModule)
        install(EntityHitModule)
        install(ExceptionHandlerModule)
        install(GameMapModule)
        install(HuntModule)
        install(MarketModule)
        install(PlayerModule)
        install(PasswordHashModule)
        install(RandomModule)
        install(RealmModule)
        install(RegistryModule)
        install(RouteModule)
        install(ServerConfigModule)
        install(StatModModule)
        install(TotpModule)
        install(TypeModule)
        bindInstance<GameCycle>()
        bindInstance<WorldQueueList>()
        bindProvider(DbRowResolverProvider::class.java)
        bindProvider(EnumTypeMapResolverProvider::class.java)
    }

    private class DbRowResolverProvider @Inject constructor(private val types: TypeListMap) :
        Provider<DbRowResolver> {
        override fun get(): DbRowResolver = DbRowResolver(types)
    }

    private class EnumTypeMapResolverProvider @Inject constructor(private val enums: EnumTypeList) :
        Provider<EnumTypeMapResolver> {
        override fun get(): EnumTypeMapResolver = EnumTypeMapResolver(enums)
    }
}
