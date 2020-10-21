package gg.rsmod.plugins.content.serializer.module

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule
import gg.rsmod.game.model.domain.serializer.ClientDataMapper
import gg.rsmod.plugins.content.serializer.DefaultClientMapper

class DefaultSerializerModule(
    private val scope: Scope
) : KotlinModule() {

    override fun configure() {
        bind<ClientDataMapper<*>>()
            .to<DefaultClientMapper>()
            .`in`(scope)
    }
}
