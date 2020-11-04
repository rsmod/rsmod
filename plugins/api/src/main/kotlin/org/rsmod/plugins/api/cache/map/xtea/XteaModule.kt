package org.rsmod.plugins.api.cache.map.xtea

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.rsmod.game.model.domain.repo.XteaRepository
import org.rsmod.plugins.api.cache.map.xtea.loader.XteaFileLoader
import org.rsmod.plugins.api.cache.map.xtea.repo.XteaInMemoryRepository

class XteaModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<XteaRepository>()
            .to<XteaInMemoryRepository>()
            .`in`(scope)

        bind<XteaFileLoader>()
            .`in`(scope)
    }
}
