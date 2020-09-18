package gg.rsmod.plugins.core.serializer

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import gg.rsmod.game.model.domain.serializer.ClientDataMapper

class DefaultSerializerModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<ClientDataMapper<*>>()
            .to<DefaultClientMapper>()
            .`in`(scope)
    }
}
