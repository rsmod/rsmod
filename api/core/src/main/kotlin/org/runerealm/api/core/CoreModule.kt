package org.runerealm.api.core

import com.google.inject.Provider
import jakarta.inject.Inject
import org.runerealm.api.account.AccountModule
import org.runerealm.api.area.checker.AreaCheckerModule
import org.runerealm.api.cache.CacheModule
import org.runerealm.api.core.module.DropTableModule
import org.runerealm.api.core.module.EntityHitModule
import org.runerealm.api.core.module.GameMapModule
import org.runerealm.api.core.module.PlayerModule
import org.runerealm.api.core.module.RealmModule
import org.runerealm.api.core.module.RegistryModule
import org.runerealm.api.core.module.StatModModule
import org.runerealm.api.core.module.TypeModule
import org.runerealm.api.db.DatabaseModule
import org.runerealm.api.game.process.GameCycle
import org.runerealm.api.hunt.HuntModule
import org.runerealm.api.market.MarketModule
import org.runerealm.api.pw.hash.PasswordHashModule
import org.runerealm.api.random.RandomModule
import org.runerealm.api.route.RouteModule
import org.runerealm.api.server.config.ServerConfigModule
import org.runerealm.api.totp.TotpModule
import org.runerealm.api.utils.logging.ExceptionHandlerModule
import org.runerealm.game.dbtable.DbRowResolver
import org.runerealm.game.dbtable.DbTableResolver
import org.runerealm.game.enums.EnumTypeMapResolver
import org.runerealm.game.queue.WorldQueueList
import org.runerealm.game.type.TypeListMap
import org.runerealm.game.type.enums.EnumTypeList
import org.runerealm.module.ExtendedModule

public object CoreModule : ExtendedModule() {
    override fun bind() {
        install(AccountModule)
        install(AreaCheckerModule)
        install(CacheModule)
        install(DatabaseModule)
        install(DropTableModule)
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
        bindProvider(DbTableResolverProvider::class.java)
        bindProvider(EnumTypeMapResolverProvider::class.java)
    }

    private class DbRowResolverProvider @Inject constructor(private val types: TypeListMap) :
        Provider<DbRowResolver> {
        override fun get(): DbRowResolver = DbRowResolver(types)
    }

    private class DbTableResolverProvider @Inject constructor(private val types: TypeListMap) :
        Provider<DbTableResolver> {
        override fun get(): DbTableResolver = DbTableResolver(types)
    }

    private class EnumTypeMapResolverProvider @Inject constructor(private val enums: EnumTypeList) :
        Provider<EnumTypeMapResolver> {
        override fun get(): EnumTypeMapResolver = EnumTypeMapResolver(enums)
    }
}
